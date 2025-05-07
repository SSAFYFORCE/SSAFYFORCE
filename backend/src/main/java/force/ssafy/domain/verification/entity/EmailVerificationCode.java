package force.ssafy.domain.verification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // -> @Entity JPA 할때 필요하다고 들었던거같기도해 : 이렇게 동작하는지 테스트 필요
@Builder
@ToString
public class EmailVerificationCode {

    @Id
    @Column(length = 30)
    private String solvedAcId;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}