package force.ssafy.domain.teamJoinRequest.dto;

import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamJoinRequestDto {

    private Long memberId;
    private Long teamId;
    private String name;
    private String profileImageUrl;

    public static TeamJoinRequestDto from(TeamJoinRequest request) {
        return TeamJoinRequestDto.builder()
                .memberId(request.getRequester().getId())
                .teamId(request.getTeam().getId())
                .name(request.getRequester().getName())
                .profileImageUrl(request.getRequester().getProfileImageUrl())
                .build();
    }
}
