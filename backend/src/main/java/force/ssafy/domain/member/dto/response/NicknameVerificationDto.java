package force.ssafy.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameVerificationDto {
    private boolean available;
    private String message;
}