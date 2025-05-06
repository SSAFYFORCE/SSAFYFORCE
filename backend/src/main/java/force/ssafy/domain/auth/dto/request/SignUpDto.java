package force.ssafy.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 3, max = 30, message = "닉네임은 3자 이상 30자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 30, message = "이름은 30자 이하로 입력해주세요.")
    private String name;

    @NotNull(message = "클래스 레벨은 필수 입력값입니다.")
    private Integer classLevel;

    @NotBlank(message = "solved.ac ID는 필수 입력값입니다.")
    private String solvedAcId;
}