package force.ssafy.domain.solvedProblem.service;

import force.ssafy.domain.solvedProblem.controller.dto.response.InfiniteScrollResponse;
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

    public InfiniteScrollResponse<SolvedProblemResponse> getRecentSolvedProblemsWithCursor(
            String solvedAcId,
            LocalDateTime cursor) {
        log.info("무한스크롤 조회 - solvedAcId: {}, cursor: {}", solvedAcId, cursor);
        final int PAGE_SIZE = 20;

        List<SolvedProblem> solvedProblems;
        boolean isFirst = (cursor == null);
        if (isFirst) {
            // 첫번째 데이터 요청 : 최신부터
            solvedProblems = solvedProblemRepository.findTop21ByMemberSolvedAcIdOrderBySolvedDateDesc(solvedAcId);
        } else {
            solvedProblems = solvedProblemRepository.findTop21ByMemberSolvedAcIdAndSolvedDateLessThanOrderBySolvedDateDesc(solvedAcId, cursor);
        }
        boolean hasNext = solvedProblems.size() > PAGE_SIZE;
        List<SolvedProblem> actualContent = hasNext ? solvedProblems.subList(0, PAGE_SIZE) : solvedProblems;

        // 다음 커서
        String nextCursor = null;
        if (hasNext && !actualContent.isEmpty()) {
            LocalDateTime lastSolvedDate = actualContent.get(actualContent.size() - 1).getSolvedDate();
            nextCursor = lastSolvedDate.toString();
        }
        List<SolvedProblemResponse> responseContent = actualContent.stream().map(SolvedProblemResponse::from).toList();
        log.info("무한스크롤 조회 결과 - 조회: {}개, 반환: {}개, hasNext: {}, nextCursor: {}",
                solvedProblems.size(), responseContent.size(), hasNext, nextCursor);
        return isFirst ?
                InfiniteScrollResponse.first(responseContent, hasNext, nextCursor, PAGE_SIZE) :
                InfiniteScrollResponse.next(responseContent, hasNext, nextCursor, PAGE_SIZE);
    }

    private Page<SolvedProblem> findSolvedProblems(Long memberId,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate,
                                                   Pageable pageable) {
        if (memberId != null && startDate != null && endDate != null) {
            return solvedProblemRepository.findByMemberIdAndSolvedDateBetween(memberId, startDate, endDate, pageable);
        } else if (memberId != null) {
            return solvedProblemRepository.findByMemberId(memberId, pageable);
        } else if (startDate != null && endDate != null) {
            return solvedProblemRepository.findBySolvedDateBetween(startDate, endDate, pageable);
        } else {
            return solvedProblemRepository.findAll(pageable);
        }
    }

}
