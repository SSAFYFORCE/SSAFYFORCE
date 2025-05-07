package force.ssafy.domain.member.exception;

import force.ssafy.global.error.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateSolvedAcIdException extends BusinessException {
    public DuplicateSolvedAcIdException(String solvedAcId) {
        super("이미 등록된 solved.ac ID입니다: " + solvedAcId, HttpStatus.BAD_REQUEST);
    }
}