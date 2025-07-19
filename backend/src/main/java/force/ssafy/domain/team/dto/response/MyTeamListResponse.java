package force.ssafy.domain.team.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTeamListResponse {

    private int teamCount;
    private List<TeamSimpleResponse> teams;

    public static MyTeamListResponse from(List<TeamSimpleResponse> teamList) {
        return MyTeamListResponse.builder()
                .teamCount(teamList.size())
                .teams(teamList)
                .build();
    }
}