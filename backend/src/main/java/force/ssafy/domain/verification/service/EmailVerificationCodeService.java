package force.ssafy.domain.verification.service;

import force.ssafy.domain.solvedac.exception.SolvedAcVerificationException;
import force.ssafy.domain.verification.dto.EmailVerificationCodeDto;
import force.ssafy.domain.verification.entity.EmailVerificationCode;
import force.ssafy.domain.verification.repository.EmailVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationCodeService {

    private final EmailVerificationCodeRepository verificationCodeRepository;

    public EmailVerificationCodeDto findById(String id) {
        EmailVerificationCode verificationCode = verificationCodeRepository.findById(id)
                .orElseThrow(() -> new SolvedAcVerificationException("발급된 인증코드가 없습니다. 먼저 인증코드를 발급받으세요."));
        return EmailVerificationCodeDto.of(verificationCode);
    }
}
