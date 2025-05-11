package force.ssafy.domain.solvedProblem.service;

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
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolvedProblemSyncService {
    private final SolvedProblemRepository solvedProblemRepository;
    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;

    @Value("${aws.lambda.baekjoon-crawler-url")
    private String lambdaUrl;

    @Transactional
    public SyncResultResponse syncSolvedProblems(Long memberId) {

        // 1. 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // 2. 마지막 동기화 시간 확인 (없으면 10년 전으로 설정)
        LocalDateTime lastSyncTime = null;
        lastSyncTime = member.getLastProblemSyncTime();
        if (lastSyncTime == null) {
            lastSyncTime = LocalDateTime.now().minusYears(10);
        }

        // 3. AWS Lambda 호출을 위한 요청 생성
        CrawlRequestDto request = CrawlRequestDto.of(member, lastSyncTime);

        // 4. Lambda 호출하여 새로운 해결 문제 데이터 가져오기
        List<SolvedProblemDto> newSolvedProblems = callLambdaFunction(request);

        // 5. 가져온 데이터 처리
        int savedCount = processSolvedProblems(member, newSolvedProblems);

        // 6. 마지막 동기화 시간 업데이트
        member.updateLastProblemSyncTime(LocalDateTime.now());
        memberRepository.save(member);

        // 7. 결과 반환
        return new SyncResultResponse(savedCount);

    }

    private List<SolvedProblemDto> callLambdaFunction(CrawlRequestDto request) {
        try {
            WebClient webClient = WebClient.builder().baseUrl(lambdaUrl).build();
            return webClient.post().bodyValue(request).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SolvedProblemDto>>() {
                    }).block();
        } catch (Exception e) {
            log.error("람다 함수 호출 실패(callLambdaFunction) : {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private int processSolvedProblems(Member member, List<SolvedProblemDto> newSolvedProblems) {
        int savedCount = 0;
        for (SolvedProblemDto dto : newSolvedProblems) {
            if (solvedProblemRepository.existsBySubmissionId(dto.submissionId())) {
                log.debug("이미 존재하는 submission 입니다. :{}", dto.submissionId());
                continue;
            }
            // 오류 로그로 남기고 continue로 다음 문제 계속 진행하는 것이 좋을 듯
            Problem problem = problemRepository.findByProblemNumber(dto.problemNumber())
                    .orElse(null);
            if (problem == null) {
                log.warn("문제를 찾을 수 없습니다. problemNumber:{}", dto.problemNumber());
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
        }
        return savedCount;
    }
}
