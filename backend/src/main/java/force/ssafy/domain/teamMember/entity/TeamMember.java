package force.ssafy.domain.teamMember.entity;

import force.ssafy.domain.team.entity.Team;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    @Enumerated(EnumType.STRING)
    private MemberRole role;
}
