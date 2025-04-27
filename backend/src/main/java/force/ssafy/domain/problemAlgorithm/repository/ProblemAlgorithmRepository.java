package force.ssafy.domain.problemAlgorithm.repository;

import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemAlgorithmRepository extends JpaRepository<ProblemAlgorithm, Long> {

    List<ProblemAlgorithm> findByProblem_Id(Long problemId);
}
