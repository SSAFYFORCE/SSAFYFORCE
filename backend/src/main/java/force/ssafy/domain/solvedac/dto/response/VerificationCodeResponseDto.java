package force.ssafy.domain.solvedac.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationCodeResponseDto {
    private String solvedAcId;
    private String verificationCode;
    private String message = "인증코드를 solved.ac 프로필의 이름 항목에 입력해주세요.";
}
