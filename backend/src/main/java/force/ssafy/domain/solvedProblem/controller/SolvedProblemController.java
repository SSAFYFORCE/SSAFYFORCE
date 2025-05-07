package force.ssafy.domain.solvedProblem.controller;

import force.ssafy.domain.solvedProblem.controller.dto.response.SolvedProblemResponse;
import force.ssafy.domain.solvedProblem.service.SolvedProblemService;
import force.ssafy.global.util.DateUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/solved-problems")
public class SolvedProblemController {
    private final SolvedProblemService solvedProblemService;
    @GetMapping
    public ResponseEntity<Page<SolvedProblemResponse>> getAllSolvedProblems(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
            ) {
        DateUtils.validateDataRange(startDate, endDate);
        return ResponseEntity.ok(solvedProblemService.getSolvedProblems(memberId, startDate, endDate, pageable));
    }
}