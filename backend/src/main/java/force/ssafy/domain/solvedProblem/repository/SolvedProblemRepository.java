package force.ssafy.domain.solvedProblem.repository;

import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {

    Page<SolvedProblem> findAll(Pageable pageable);

    Page<SolvedProblem> findByMemberId(Long memberId, Pageable pageable);

    Page<SolvedProblem> findBySolvedDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    Page<SolvedProblem> findByMemberIdAndSolvedDate(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

}
