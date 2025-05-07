package force.ssafy.domain.member.entity;

import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.teamMember.entity.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, length = 30, unique = true)
//    private String nickname;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 200)
    private String profileImage;

    @Column(nullable = false, length = 100)
    private String encryptionKey;

    @Column(name = "solved_ac_id", nullable = false, length = 30, unique = true)
    private String solvedAcId;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member")
    private List<TeamMember> teamMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<SolvedProblem> SolvedProblems = new ArrayList<>();

    @Builder
    public Member( String password, String name,
                  String profileImage, String encryptionKey, String solvedAcId) {
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
        this.encryptionKey = encryptionKey;
        this.solvedAcId = solvedAcId;
    }

    public void verify() {
        this.verified = true;
    }

    public void updateProfile(String name, String profileImage) {
        this.name = name;
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    //이부분 수정필요 실행안되서 UserDetails 오버라이드 받음
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.solvedAcId;
    }
}