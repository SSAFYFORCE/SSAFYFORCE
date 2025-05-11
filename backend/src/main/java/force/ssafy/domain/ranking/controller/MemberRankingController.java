package force.ssafy.domain.ranking.controller;

import force.ssafy.domain.ranking.controller.dto.response.RankingResponse;
import force.ssafy.domain.ranking.service.MemberRankingService;
import force.ssafy.domain.ranking.service.dto.RankingDto;
import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/rankings/member")
@RequiredArgsConstructor
public class MemberRankingController {
    private final MemberRankingService memberRankingService;

    @GetMapping("/daily")
    public ResponseEntity<RankingResponse> getDailyRanking(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(memberRankingService.getDailyRanking(targetDate) );
    }

    @GetMapping("/weekly")
    public ResponseEntity<RankingResponse> getWeeklyRanking(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(memberRankingService.getWeeklyRanking(targetDate) );
    }

    @GetMapping("/monthly")
    public ResponseEntity<RankingResponse> getMonthlyRanking(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(memberRankingService.getMonthlyRanking(targetDate) );
    }
}
