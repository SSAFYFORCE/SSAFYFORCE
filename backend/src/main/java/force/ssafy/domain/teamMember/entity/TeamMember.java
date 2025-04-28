package force.ssafy.domain.teamMember.entity;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.team.entity.Team;
import jakarta.persistence.*;

@Entity
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
    private String joinedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // 멤버 기본 역할은 팀원
    private MemberRole role = MemberRole.MEMBER;
}
