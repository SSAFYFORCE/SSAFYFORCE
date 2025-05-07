package force.ssafy.domain.solvedac.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationResultDto {
    private boolean verified;
    private String message;
}