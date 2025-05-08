package force.ssafy.domain.member.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String solvedAcId;


    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String newPassword;
}
