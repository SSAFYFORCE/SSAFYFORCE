package force.ssafy.domain.teamJoinRequest.entity;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamJoinRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;   // 팀 생성자 or 현 리더

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private JoinStatus status = JoinStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime decidedAt;

    // ------- 비즈니스 메서드 -------
    public void approve() {
        ensurePending();
        this.status = JoinStatus.APPROVED;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject() {
        ensurePending();
        this.status = JoinStatus.REJECTED;
        this.decidedAt = LocalDateTime.now();
    }

    private void ensurePending() {
        if (this.status != JoinStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");
    }
}
