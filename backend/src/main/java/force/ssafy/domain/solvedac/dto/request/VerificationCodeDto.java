package force.ssafy.domain.solvedac.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class VerificationCodeDto {

    @NotBlank(message = "solved.ac 아이디는 필수 입력값입니다.")
    private String solvedAcId;
}