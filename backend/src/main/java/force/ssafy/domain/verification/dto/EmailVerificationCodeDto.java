package force.ssafy.domain.verification.dto;


import force.ssafy.domain.verification.entity.EmailVerificationCode;

import java.time.LocalDateTime;

public record EmailVerificationCodeDto(
        String solvedAcId,
        String code,
        LocalDateTime expiryDate,
        boolean expired
) {
    public static EmailVerificationCodeDto of(EmailVerificationCode entity) {
        return new EmailVerificationCodeDto(
                entity.getSolvedAcId(),
                entity.getCode(),
                entity.getExpiryDate(),
                entity.isExpired()
        );
    }
}