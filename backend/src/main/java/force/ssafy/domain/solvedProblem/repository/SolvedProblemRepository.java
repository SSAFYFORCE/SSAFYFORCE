package force.ssafy.domain.solvedProblem.repository;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    Page<SolvedProblem> findByMemberIdAndSolvedDateBetween(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
    boolean existsBySubmissionId(Integer submissionId);

    boolean existsByMemberAndProblem(Member member, Problem problem);

    List<SolvedProblem> findBySolvedDateBetweenAndIsFirstSolved(LocalDateTime startDate, LocalDateTime endDate, Boolean isFirstSolved);

    @EntityGraph(attributePaths = {"member", "problem"}) // fetch join 사용하기
    List<SolvedProblem> findTop21ByMemberSolvedAcIdOrderBySolvedDateDesc(String solvedAcId);

    @EntityGraph(attributePaths = {"member", "problem"})
    List<SolvedProblem> findTop21ByMemberSolvedAcIdAndSolvedDateLessThanOrderBySolvedDateDesc(String solvedAcId, LocalDateTime cursor);
}
