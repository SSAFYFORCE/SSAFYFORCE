package force.ssafy.domain.solvedProblem.controller;

import force.ssafy.domain.solvedProblem.controller.dto.response.InfiniteScrollResponse;
import force.ssafy.domain.solvedProblem.controller.dto.response.SolvedProblemResponse;
import force.ssafy.domain.solvedProblem.controller.dto.response.SyncResultResponse;
import force.ssafy.domain.solvedProblem.service.SolvedProblemService;
import force.ssafy.domain.solvedProblem.service.SolvedProblemSyncService;
import force.ssafy.global.util.DateUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/solved-problems")
public class SolvedProblemController {
    private final SolvedProblemService solvedProblemService;
    private final SolvedProblemSyncService solvedProblemSyncService;

    @GetMapping
    public ResponseEntity<Page<SolvedProblemResponse>> getAllSolvedProblems(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        DateUtils.validateDataRange(startDate, endDate);
        return ResponseEntity.ok(solvedProblemService.getSolvedProblems(memberId, startDate, endDate, pageable));
    }

    @GetMapping("/recent/{solvedAcId}")
    public ResponseEntity<InfiniteScrollResponse<SolvedProblemResponse>> getRecentSolvedProblemsWithScroll(
            @PathVariable("solvedAcId") String solvedAcId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime cursor){
        log.info("무한스크롤 조회 요청 - solvedAcId: {}, cursor: {}", solvedAcId, cursor);
        InfiniteScrollResponse<SolvedProblemResponse> response = solvedProblemService.getRecentSolvedProblemsWithCursor(solvedAcId, cursor);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync/{solvedAcId}")
    public ResponseEntity<SyncResultResponse> syncSolvedProblems(@PathVariable("solvedAcId") String solvedAcId) {
        SyncResultResponse syncResultResponse = solvedProblemSyncService.syncSolvedProblems(solvedAcId);
        return ResponseEntity.ok(syncResultResponse);
    }

    @PostMapping("/sync-async/{solvedAcId}")
    public Mono<ResponseEntity<SyncResultResponse>> syncSolvedProblemsAsync(@PathVariable("solvedAcId") String solvedAcId) {
        log.info("비동기 동기화 요청 - solvedAcId: {}", solvedAcId);
        return solvedProblemSyncService.syncSolvedProblemsAsync(solvedAcId)
            .map(ResponseEntity::ok)
            .doOnSuccess(response ->
                log.info("비동기 동기화 요청 완료 - solvedAcId: {}", solvedAcId))
            .doOnError(error ->
                log.error("비동기 동기화 요청 실패 - solvedAcId: {}, error: {}", solvedAcId, error.getMessage()));
    }
}