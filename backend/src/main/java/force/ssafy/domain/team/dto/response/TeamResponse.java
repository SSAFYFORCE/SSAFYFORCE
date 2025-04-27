package force.ssafy.domain.team.dto.response;

import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamResponse {

    private int group;
    private List<TeamMemberResponse> teamMembers;

    public static TeamResponse from(int group, List<TeamMemberResponse> teamMembers) {
        return TeamResponse.builder()
                .group(group)
                .teamMembers(teamMembers)
                .build();
    }
}
