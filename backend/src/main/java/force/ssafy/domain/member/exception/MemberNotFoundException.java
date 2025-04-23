package force.ssafy.domain.member.exception;

import force.ssafy.global.error.exception.EntityNotFoundException;

public class MemberNotFoundException extends EntityNotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}