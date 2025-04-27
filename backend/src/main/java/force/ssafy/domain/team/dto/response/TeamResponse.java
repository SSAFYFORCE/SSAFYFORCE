package force.ssafy.domain.team.dto.response;

import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamResponse {

    private String name;
    private List<TeamMemberResponse> teamMembers;

    public static TeamResponse from(String name, List<TeamMemberResponse> teamMembers) {
        return TeamResponse.builder()
                .name(name)
                .teamMembers(teamMembers)
                .build();
    }
}
