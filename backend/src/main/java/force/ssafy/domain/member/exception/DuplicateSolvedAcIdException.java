package force.ssafy.domain.member.exception;

public class DuplicateSolvedAcIdException extends RuntimeException {
    public DuplicateSolvedAcIdException(String solvedAcId) {
        super("이미 등록된 solved.ac ID입니다: " + solvedAcId);
    }
}