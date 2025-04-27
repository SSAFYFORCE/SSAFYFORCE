package force.ssafy.domain.teamMember.dto.response;

import force.ssafy.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberResponse {

    private Long id;
    private String nickname;
    private String name;
    private String profileImage;

    public static TeamMemberResponse from(Member member) {
        return TeamMemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .build();
    }
}
