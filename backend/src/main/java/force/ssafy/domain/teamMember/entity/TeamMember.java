package force.ssafy.domain.teamMember.entity;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // 멤버 기본 역할은 팀원
    private MemberRole role = MemberRole.MEMBER;

    public static TeamMember create(Member member, Team team) {
        TeamMember tm = TeamMember.builder()
                .member(member)
                .team(team)
                .joinedAt(LocalDateTime.now())
                .role(MemberRole.MEMBER)
                .build();

        // 양방향 관계 동기화
        member.getTeamMembers().add(tm);
        team.getTeamMembers().add(tm);
        return tm;
    }
}
