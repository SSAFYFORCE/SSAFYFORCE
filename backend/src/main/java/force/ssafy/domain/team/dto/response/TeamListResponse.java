package force.ssafy.domain.team.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamListResponse {

    private int teamCount;
    private List<TeamResponse> teams;

    public static TeamListResponse to(List<TeamResponse> teamResponseList) {
        return TeamListResponse.builder()
                .teamCount(teamResponseList.size())
                .teams(teamResponseList)
                .build();
    }
}
