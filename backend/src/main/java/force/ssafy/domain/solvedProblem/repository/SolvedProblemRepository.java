package force.ssafy.domain.solvedProblem.repository;

import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {
}
