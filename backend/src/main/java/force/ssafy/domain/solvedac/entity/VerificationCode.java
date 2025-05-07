package force.ssafy.domain.solvedac.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VerificationCode {

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