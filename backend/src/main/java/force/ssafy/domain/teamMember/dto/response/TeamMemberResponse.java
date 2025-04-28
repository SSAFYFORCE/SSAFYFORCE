package force.ssafy.domain.teamMember.dto.response;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberResponse {

    private Long id;
    private String nickname;
    private String name;
    private String profileImage;

    public static TeamMemberResponse from(TeamMemberDto teamMemberDto) {
        return TeamMemberResponse.builder()
                .id(teamMemberDto.getId())
                .nickname(teamMemberDto.getNickname())
                .name(teamMemberDto.getName())
                .profileImage(teamMemberDto.getProfileImage())
                .build();
    }
}
