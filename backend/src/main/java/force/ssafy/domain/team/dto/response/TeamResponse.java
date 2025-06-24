package force.ssafy.domain.team.dto.response;

import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TeamResponse {

    private long teamId;
    private String name;
    private String description;
    private int memberCount;
//    private String leader;
    private LocalDateTime createdAt;
    private List<TeamMemberResponse> teamMembers;

    public static TeamResponse of(Team team, List<TeamMemberResponse> teamMembers) {
        return TeamResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .memberCount(teamMembers.size())
                .createdAt(team.getCreatedAt())
                .teamMembers(teamMembers)
                .build();
    }
}
