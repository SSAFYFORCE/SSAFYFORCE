package force.ssafy.domain.solvedac.repository;

import force.ssafy.domain.solvedac.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

}