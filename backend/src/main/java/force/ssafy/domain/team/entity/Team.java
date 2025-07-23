package force.ssafy.domain.team.entity;

import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.teamMember.entity.TeamMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private String profileImage;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMembers = new ArrayList<>();

    private boolean deleted = false;

    private LocalDateTime deletedAt;

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public Long getLeaderId() {
        return this.teamMembers.stream()
                .filter(tm -> tm.getRole() == MemberRole.LEADER)   // ① LEADER 비교
                .findFirst()
                .map(tm -> tm.getMember().getId())                 // ② member id 반환
                .orElseThrow(() -> new IllegalStateException("리더 없음"));
    }
}
