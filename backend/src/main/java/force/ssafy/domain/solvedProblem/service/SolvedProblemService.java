package force.ssafy.domain.solvedProblem.service;

import force.ssafy.domain.solvedProblem.controller.dto.response.SolvedProblemResponse;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolvedProblemService {

    private final SolvedProblemRepository solvedProblemRepository;

    public Page<SolvedProblemResponse> getSolvedProblems(Long memberId,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate,
                                                         Pageable pageable) {
        Page<SolvedProblem> solvedProblems = findSolvedProblems(memberId, startDate, endDate, pageable);
        return solvedProblems.map(sp -> SolvedProblemResponse.from(sp));
    }

    private Page<SolvedProblem> findSolvedProblems(Long memberId,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate,
                                                   Pageable pageable) {
        if (memberId != null && startDate != null && endDate != null) {
            return solvedProblemRepository.findByMemberIdAndSolvedDate(memberId, startDate, endDate, pageable);
        } else if (memberId != null) {
            return solvedProblemRepository.findByMemberId(memberId, pageable);
        } else if (startDate != null && endDate != null) {
            return solvedProblemRepository.findBySolvedDateBetween(startDate, endDate, pageable);
        } else {
            return solvedProblemRepository.findAll(pageable);
        }
    }

}
