package force.ssafy.domain.problem.repository;

import force.ssafy.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Query(
            value = "SELECT problem_number FROM problem ORDER BY problem_number DESC LIMIT 1",
            nativeQuery = true
    )
    Long findMaxProblemNumber();

}
