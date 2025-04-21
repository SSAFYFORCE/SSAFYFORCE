package force.ssafy.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberDto {
    private Long id;
    private String nickname;
    private String name;
    private Integer classLevel;
    private String profileImage;
    private LocalDateTime createdAt;
    private String solvedAcId;
}
