package force.ssafy.domain.member.exception;

import force.ssafy.global.error.exception.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {
    public MemberNotFoundException(Long id) {
        super("회원을 찾을 수 없습니다. ID: " + id);
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}