package force.ssafy.domain.solvedac.service;

import force.ssafy.domain.solvedac.dto.response.VerificationCodeResponseDto;
import force.ssafy.domain.solvedac.dto.response.VerificationResultDto;
import force.ssafy.domain.solvedac.entity.VerificationCode;
import force.ssafy.domain.solvedac.exception.SolvedAcVerificationException;
import force.ssafy.domain.solvedac.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

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
    @Transactional
    public VerificationResultDto verifyCode(String solvedAcId) {
        // 저장된 인증코드 조회
        VerificationCode verificationCode = verificationCodeRepository.findById(solvedAcId)
                .orElseThrow(() -> new SolvedAcVerificationException("발급된 인증코드가 없습니다. 먼저 인증코드를 발급받으세요."));

        // 인증코드 만료 여부 확인
        if (verificationCode.isExpired()) {
            verificationCodeRepository.delete(verificationCode);
            throw new SolvedAcVerificationException("인증코드가 만료되었습니다. 새로운 인증코드를 발급받으세요.");
        }

        // 실제 구현에서는 solved.ac API를 호출하여 사용자 프로필의 이름 필드에
        // 인증코드가 올바르게 설정되었는지 확인
        boolean verified = checkSolvedAcProfile(solvedAcId, verificationCode.getCode());

        if (verified) {
            // 인증 성공 시 코드 제거
            verificationCodeRepository.delete(verificationCode);
        }

        return VerificationResultDto.builder()
                .verified(verified)
                .message(verified ? "인증에 성공했습니다." : "인증에 실패했습니다. 프로필 설정을 확인해주세요.")
                .build();
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
}