package force.ssafy.domain.teamMember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMemberDto {
    private Long id;
    private String nickname;
    private String name;
    private String profileImage;
}
