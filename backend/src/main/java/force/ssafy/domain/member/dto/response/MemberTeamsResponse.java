package force.ssafy.domain.member.dto.response;

import force.ssafy.domain.team.dto.response.TeamSimpleResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberTeamsResponse {
    private List<TeamSimpleResponse> teams;

    public static MemberTeamsResponse from(List<TeamSimpleResponse> teams) {
        return MemberTeamsResponse.builder()
                .teams(teams)
                .build();
    }
}
