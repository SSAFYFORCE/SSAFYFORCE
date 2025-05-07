package force.ssafy.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInDto {

    @NotBlank(message = "solved.ac ID는 필수 입력값입니다.")
    private String solvedAcId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}