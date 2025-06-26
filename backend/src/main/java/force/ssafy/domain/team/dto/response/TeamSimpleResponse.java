package force.ssafy.domain.team.dto.response;

import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.teamMember.entity.TeamMember;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamSimpleResponse {
    private Long id;
    private String name;
    private String profileImage;

    public static TeamSimpleResponse from(TeamMember tm) {
        Team t = tm.getTeam();
        return TeamSimpleResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .profileImage(t.getProfileImage())
                .build();
    }
}
