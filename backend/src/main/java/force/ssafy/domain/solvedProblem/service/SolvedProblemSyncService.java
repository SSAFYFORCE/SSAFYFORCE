package force.ssafy.domain.solvedProblem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.repository.ProblemRepository;
import force.ssafy.domain.solvedProblem.controller.dto.response.SyncResultResponse;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;
import force.ssafy.domain.solvedProblem.service.dto.CrawlRequestDto;
import force.ssafy.domain.solvedProblem.service.dto.SolvedProblemDto;
import force.ssafy.global.error.exception.EntityNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolvedProblemSyncService {
    private final SolvedProblemRepository solvedProblemRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;


    @Value("${aws.lambda.baekjoon-crawler-url}")
    private String lambdaUrl;

    @Transactional(propagation = Propagation.REQUIRED)
    public SyncResultResponse syncSolvedProblems(String solvedAcId) {

        // 1. 회원 정보 조회
        Member member = memberRepository.findBySolvedAcId(solvedAcId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 2. 마지막 동기화 시간 확인 (없으면 10년 전으로 설정)
        LocalDateTime lastSyncTime = member.getLastProblemSyncTime();
        if (lastSyncTime == null) {
            lastSyncTime = LocalDateTime.now().minusYears(10);
        }

        // 3. AWS Lambda 호출을 위한 요청 생성
        CrawlRequestDto request = CrawlRequestDto.of(member, lastSyncTime);

        // LocalDateTime을 ISO 문자열로 변환하여 전송
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("solvedAcId", request.solvedAcId());
        requestMap.put("lastSyncTime", lastSyncTime.toString()); // ISO 8601 형식으로 변환

        // 4. Lambda 호출하여 새로운 해결 문제 데이터 가져오기
        List<SolvedProblemDto> newSolvedProblems;
        try {
            newSolvedProblems = callLambdaFunction(requestMap);
        } catch (Exception e) {
            log.error("Lambda 함수 호출 실패 - 동기화 중단: {}", e.getMessage());
            throw e; // 예외를 다시 던져서 트랜잭션 롤백
        }

        // 5. 가져온 데이터 처리
        int savedCount;
        try {
            savedCount = processSolvedProblems(member, newSolvedProblems);
        } catch (Exception e) {
            log.error("문제 데이터 처리 실패 - 동기화 중단: {}", e.getMessage());
            throw e; // 예외를 다시 던져서 트랜잭션 롤백
        }

        // 6. 모든 작업이 성공했을 때만 마지막 동기화 시간 업데이트
        try {
            member.updateLastProblemSyncTime(LocalDateTime.now());
            log.info("동기화 성공 - 저장된 문제 수: {}, 사용자: {}", savedCount, solvedAcId);
        } catch (Exception e) {
            log.error("동기화 시간 업데이트 실패: {}", e.getMessage());
            throw e; // 예외를 다시 던져서 트랜잭션 롤백
        }

        // 7. 결과 반환
        return new SyncResultResponse(savedCount);
    }

    private List<SolvedProblemDto> callLambdaFunction(Map<String, Object> request) {
        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl(lambdaUrl)
                    .codecs(configurer ->
                            configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024)
                    )
                    .build();

            String response = webClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMinutes(3))
                    .doOnSubscribe(subscription -> log.info("요청 전송 완료, 응답 대기 중..."))
                    .doOnNext(resp -> log.info("Lambda 응답 수신 완료 - 크기: {} bytes ({} KB)",
                            resp.length(), resp.length() / 1024))
                    .block();

            // 응답 파싱 및 오류 처리
            return parseLambdaResponse(response);

        } catch (WebClientResponseException e) {
            log.error("람다 함수 호출 실패 - HTTP 오류코드: {}, 오류 내용: {}", e.getStatusCode(), e.getMessage());
            handleLambdaError(e);
            // handleLambdaError에서 예외를 던지므로 여기 도달하지 않음
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("람다 함수 호출 실패: {}", e.getMessage(), e);
            // 네트워크 오류나 기타 예외 시 RuntimeException으로 래핑
            throw new RuntimeException("람다 함수 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private List<SolvedProblemDto> parseLambdaResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            // 오류 응답인지 확인
            if (jsonNode.has("error") && jsonNode.get("error").asBoolean()) {
                String errorCode = jsonNode.get("errorCode").asText();
                String message = jsonNode.get("message").asText();

                log.error("람다 함수에서 오류 응답: errorCode={}, message={}", errorCode, message);
                handleErrorCode(errorCode, message);
            }

            // 정상 응답인 경우 - 직접 배열로 반환
            if (jsonNode.isArray()) {
                return objectMapper.readValue(
                        jsonNode.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, SolvedProblemDto.class)
                );
            } else {
                log.warn("예상하지 못한 응답 형태: {}", response);
                return new ArrayList<>();
            }

        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 먼저 처리
            throw e;
        } catch (RuntimeException e) {
            // 다른 RuntimeException 처리
            throw e;
        } catch (Exception e) {
            log.error("람다 응답 파싱 실패: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private void handleLambdaError(WebClientResponseException e) {
        HttpStatus statusCode = (HttpStatus) e.getStatusCode();
        String responseBody = e.getResponseBodyAsString();

        try {
            JsonNode responseNode = objectMapper.readTree(responseBody);

            // 오류 응답인지 확인
            if (responseNode.has("errorCode")) {
                String errorCode = responseNode.get("errorCode").asText();
                String message = responseNode.get("message").asText();
                handleErrorCode(errorCode, message);
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("오류 응답 파싱 실패: {}", ex.getMessage());
        }

        // 기본 오류 처리
        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new IllegalArgumentException("잘못된 요청입니다: " + responseBody);
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
            throw new RuntimeException("람다 함수 내부 오류: " + responseBody);
        } else {
            throw new RuntimeException("람다 함수 호출 실패: " + statusCode + " - " + responseBody);
        }
    }

    private void handleErrorCode(String errorCode, String message) {
        switch (errorCode) {
            case "INVALID_DATE_FORMAT":
                throw new IllegalArgumentException("날짜 형식 오류: " + message);
            case "NETWORK_ERROR":
                throw new RuntimeException("네트워크 오류: " + message);
            case "INVALID_REQUEST":
                throw new IllegalArgumentException("잘못된 요청: " + message);
            case "EXTERNAL_SERVICE_ERROR":
                throw new RuntimeException("외부 서비스 오류: " + message);
            case "INTERNAL_ERROR":
                throw new RuntimeException("람다 함수 내부 오류: " + message);
            case "INVALID_JSON":
                throw new IllegalArgumentException("JSON 파싱 오류: " + message);
            case "USER_NOT_FOUND":
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + message);
            default:
                throw new RuntimeException("람다 함수 오류: " + message);
        }
    }

    private int processSolvedProblems(Member member, List<SolvedProblemDto> newSolvedProblems) {
        if (newSolvedProblems == null || newSolvedProblems.isEmpty()) {
            log.info("처리할 새로운 문제가 없습니다.");
            return 0;
        }

        List<SolvedProblemDto> sortedProblems = newSolvedProblems.stream()
                .sorted(Comparator.comparing(SolvedProblemDto::solvedDate))
                .toList();

        log.info("데이터 정렬 완료: 총 {}개 문제를 시간순으로 정렬 ({} → {})",
                sortedProblems.size(),
                sortedProblems.isEmpty() ? "없음" : sortedProblems.get(0).solvedDate(),
                sortedProblems.isEmpty() ? "없음" : sortedProblems.get(sortedProblems.size() - 1).solvedDate());

        int savedCount = 0;
        int errorCount = 0;
        int duplicateCount = 0;

        for (SolvedProblemDto dto : sortedProblems) {
            try {
                // 이미 존재하는 제출인지 확인
                if (solvedProblemRepository.existsBySubmissionId(dto.submissionId())) {
                    log.debug("이미 존재하는 submission 입니다: {}", dto.submissionId());
                    duplicateCount++;
                    continue;
                }

                // 문제 존재 여부 확인
                Problem problem = problemRepository.findByProblemNumber(dto.problemNumber())
                        .orElse(null);
                if (problem == null) {
                    log.warn("문제를 찾을 수 없습니다. problemNumber: {}", dto.problemNumber());
                    errorCount++;
                    continue;
                }

                boolean previousSolved = solvedProblemRepository.existsByMemberAndProblem(member, problem);

                SolvedProblem solvedProblem = SolvedProblem.builder()
                        .member(member)
                        .problem(problem)
                        .solvedDate(dto.solvedDate())
                        .language(dto.language())
                        .timeComplexity(dto.timeComplexity())
                        .spaceComplexity(dto.spaceComplexity())
                        .submitUrl(dto.submitUrl())
                        .isFirstSolved(!previousSolved)
                        .submissionId(dto.submissionId())
                        .build();

                solvedProblemRepository.save(solvedProblem);
                savedCount++;

            } catch (Exception e) {
                log.error("문제 처리 중 오류 발생 - submissionId: {}, problemNumber: {}, error: {}",
                        dto.submissionId(), dto.problemNumber(), e.getMessage());
                errorCount++;
            }
        }

        log.info("문제 처리 완료 - 성공: {}, 중복: {}, 오류: {}, 전체: {}",
                savedCount, duplicateCount, errorCount, newSolvedProblems.size());

        return savedCount;
    }
}