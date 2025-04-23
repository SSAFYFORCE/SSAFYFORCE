package force.ssafy.domain.auth.exception;

import force.ssafy.global.error.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}