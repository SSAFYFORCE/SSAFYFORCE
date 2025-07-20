package force.ssafy.domain.team.dto.response;

import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.teamJoinRequest.entity.JoinStatus;
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
    private JoinStatus joinStatus;

    /**
     * 기존 메서드 - 팀 상세 조회용 (모든 멤버 정보)
     */
    public static TeamResponse of(Team team, List<TeamMemberResponse> teamMembers, JoinStatus joinStatus) {
        return TeamResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .memberCount(teamMembers.size())
                .createdAt(team.getCreatedAt())
                .teamMembers(teamMembers)
                .joinStatus(joinStatus)
                .build();
    }

    /**
     * 새로운 메서드 - 팀 목록 조회용 (미리보기 멤버 + 실제 멤버 수)
     */
    public static TeamResponse ofWithActualCount(Team team, List<TeamMemberResponse> previewMembers, int actualMemberCount, JoinStatus joinStatus) {
        return TeamResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .memberCount(actualMemberCount)  // 실제 멤버 수 사용
                .createdAt(team.getCreatedAt())
                .teamMembers(previewMembers)     // 미리보기 멤버 (최대 3명)
                .joinStatus(joinStatus)
                .build();
    }
}