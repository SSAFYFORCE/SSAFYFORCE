package force.ssafy.domain.member.exception;

import force.ssafy.global.error.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateNicknameException extends BusinessException {
    public DuplicateNicknameException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}