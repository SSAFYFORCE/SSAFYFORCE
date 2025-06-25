package force.ssafy.domain.solvedac.service;

import force.ssafy.domain.solvedac.dto.response.VerificationCodeResponseDto;
import force.ssafy.domain.solvedac.dto.response.VerificationResultDto;
import force.ssafy.domain.solvedac.entity.ProblemItem;
import force.ssafy.domain.solvedac.entity.VerificationCode;
import force.ssafy.domain.solvedac.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedAcService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final Random random = new SecureRandom();

    // 인증코드 만료 시간 (60분)
    private static final long EXPIRY_MINUTES = 60;

    /**
     * solved.ac 인증코드 생성 및 반환
     */
    @Transactional
    public VerificationCodeResponseDto generateVerificationCode(String solvedAcId) {

        // 이 로그가 제대로 출력되는지 확인
        log.info("solvedAcId = {}", solvedAcId); // <-- 이 로그 찍히는지 확인!

        // 기존에 발급된 코드가 있는지 확인하고 삭제
        verificationCodeRepository.findById(solvedAcId)
                .ifPresent(verificationCodeRepository::delete);

        // 새 인증코드 생성
        String code = generateRandomCode(8);

        // 인증코드 저장
        VerificationCode verificationCode = VerificationCode.builder()
                .solvedAcId(solvedAcId)
                .code(code)
                .expiryDate(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                .build();
        log.info("🔍 생성된 인증코드 객체: {}", verificationCode);

        verificationCodeRepository.save(verificationCode);

        return VerificationCodeResponseDto.builder()
                .solvedAcId(solvedAcId)
                .verificationCode(code)
                .message("인증코드를 solved.ac 프로필의 이름 항목에 입력해주세요.")
                .build();

    }

    /**
     * solved.ac 인증코드 검증
     */
    // 회원가입 전 과정이 순차적으로 이뤄지도록 가이드를 제공하는 방식
    @Transactional
    public VerificationResultDto verifyCode(String solvedAcId) {
        try {
            // 1. 저장된 인증코드 조회
            Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository.findById(solvedAcId);

            if (verificationCodeOpt.isEmpty()) {
                log.warn("인증코드 조회 실패: solvedAcId={}", solvedAcId);
                return VerificationResultDto.builder()
                        .verified(false)
                        .needCodeGeneration(true)
                        .message("발급된 인증코드가 없습니다. 먼저 인증코드를 발급받으세요.")
                        .build();
            }

            VerificationCode verificationCode = verificationCodeOpt.get();

            // 2. 인증코드 만료 여부 확인
            if (verificationCode.isExpired()) {
                verificationCodeRepository.delete(verificationCode);
                return VerificationResultDto.builder()
                        .verified(false)
                        .needCodeGeneration(true)
                        .message("인증코드가 만료되었습니다. 새로운 인증코드를 발급받으세요.")
                        .build();
            }

            // 3. solved.ac 프로필 확인
            log.info("인증 코드 검증 시작: solvedAcId={}, expectedCode={}", solvedAcId, verificationCode.getCode());
            boolean verified = checkSolvedAcProfile(solvedAcId, verificationCode.getCode());

            return VerificationResultDto.builder()
                    .verified(verified)
                    .needCodeGeneration(!verified)
                    .message(verified ? "인증에 성공했습니다." : "인증에 실패했습니다. 프로필 설정을 확인해주세요.")
                    .build();

        } catch (Exception e) {
            log.error("인증 검증 중 오류 발생: {}", e.getMessage(), e);
            return VerificationResultDto.builder()
                    .verified(false)
                    .needCodeGeneration(false)
                    .message("인증 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                    .build();
        }
    }

    // 인증 코드 삭제 메소드 추가
    @Transactional
    public void deleteVerificationCode(String solvedAcId) {
        verificationCodeRepository.findById(solvedAcId)
                .ifPresent(verificationCodeRepository::delete);
    }

    // 랜덤 인증코드 생성 메서드
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    // solved.ac 프로필 확인 메서드
    // 실제 구현에서는 solved.ac API 호출
    private boolean checkSolvedAcProfile(String solvedAcId, String expectedCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            log.info("인증 코드 검증 시작: solvedAcId={}, expectedCode={}", solvedAcId, expectedCode);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://solved.ac/api/v3/user/additional_info?handle={handle}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class,
                    solvedAcId
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("solved.ac API 호출 실패: {}", response.getStatusCode());
                return false;
            }

            Map<String, Object> userData = response.getBody();
            if (userData == null) {
                log.error("solved.ac API 응답 데이터가 없습니다.");
                return false;
            }

            // name 또는 nameNative에 인증 코드가 포함되어 있는지 확인
            String name = (String) userData.getOrDefault("name", "");
            String nameNative = (String) userData.getOrDefault("nameNative", "");

            log.info("사용자 name 필드 값: '{}', nameNative 필드 값: '{}'", name, nameNative);

            boolean verified = (name != null && name.toUpperCase().contains(expectedCode)) ||
                    (nameNative != null && nameNative.toUpperCase().contains(expectedCode));

            log.info("인증 결과: {}", verified);
            return verified;

        } catch (Exception e) {
            log.error("solved.ac 프로필 확인 중 오류 발생", e);
            return false;
        }
    }


    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://solved.ac")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .defaultHeader("x-solvedac-language", "ko")
            .build();

    public List<ProblemItem> getProblems(List<Long> problemIds) {
        String ids = problemIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return webClient.get()
                .uri("/api/v3/problem/lookup?problemIds=" + ids)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProblemItem>>() {})
                .block(); // Spring MVC에서는 block() 필요
    }

    /**
     * solved.ac 프로필 확인만 수행하는 메소드
     */
    public boolean checkSolvedAcProfile(String solvedAcId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://solved.ac/api/v3/user/additional_info?handle={handle}",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class,
                    solvedAcId
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("solved.ac API 호출 실패: {}", response.getStatusCode());
                return false;
            }

            Map<String, Object> userData = response.getBody();
            if (userData == null) {
                log.error("solved.ac API 응답 데이터가 없습니다.");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("solved.ac 프로필 확인 중 오류 발생", e);
            return false;
        }
    }

}