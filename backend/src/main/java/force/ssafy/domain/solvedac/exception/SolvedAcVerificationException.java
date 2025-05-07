package force.ssafy.domain.solvedac.exception;

import force.ssafy.global.error.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class SolvedAcVerificationException extends BusinessException {
    public SolvedAcVerificationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}