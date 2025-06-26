package force.ssafy.domain.member.dto.response;

import force.ssafy.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberProfileResponse {
    private String profileImage;
    private String name;
    private String solvedAcId;
    private LocalDateTime lastProblemSyncTime;

    public static MemberProfileResponse from(Member member) {
        return builder()
                .profileImage(member.getProfileImage())
                .name(member.getName())
                .solvedAcId(member.getSolvedAcId())
                .lastProblemSyncTime(member.getLastProblemSyncTime())
                .build();
    }
}