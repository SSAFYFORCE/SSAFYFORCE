package force.ssafy.domain.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthError {
    INVALID_USERNAME_AND_PASSWORD("아이디 또는 비밀번호가 일치하지 않습니다."),
    NOT_VERIFIED("계정 인증이 완료되지 않았습니다.");

    private final String message;

    AuthError(String message) {
        this.message = message;
    }
}