package force.ssafy.domain.auth.exception;

import force.ssafy.global.error.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(HttpStatus status) {
        super(status.getReasonPhrase(), HttpStatus.UNAUTHORIZED);
    }
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
    public AuthenticationException(AuthError error) {
        super(error.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}