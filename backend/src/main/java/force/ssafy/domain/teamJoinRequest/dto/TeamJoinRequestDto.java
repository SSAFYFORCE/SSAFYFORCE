package force.ssafy.domain.teamJoinRequest.dto;

import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamJoinRequestDto {

    private Long requestId;
    private Long memberId;
    private Long teamId;
    private String name;
    private String nickname;
    private String profileImageUrl;

    public static TeamJoinRequestDto from(TeamJoinRequest request) {
        return TeamJoinRequestDto.builder()
                .requestId(request.getId())
                .memberId(request.getRequester().getId())
                .teamId(request.getTeam().getId())
                .name(request.getRequester().getName())
                .nickname(request.getRequester().getSolvedAcId())
            .profileImageUrl(request.getRequester().getProfileImageUrl())
                .build();
    }
}
