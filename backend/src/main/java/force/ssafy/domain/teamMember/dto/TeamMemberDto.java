package force.ssafy.domain.teamMember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamMemberDto {
    private long id;
    private String nickname;
    private String name;
    private String profileImage;
}
