package force.ssafy.domain.ranking.controller;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.controller.dto.response.TeamRankingResponse;
import force.ssafy.domain.ranking.service.TeamRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/rankings/team")
@RequiredArgsConstructor
public class TeamRankingController {

    private final TeamRankingService teamRankingService;

    @GetMapping
    public ResponseEntity<TeamRankingResponse> getTeamRanking(
            @RequestParam(defaultValue = "DAILY")RankingPeriod period,
            @RequestParam(defaultValue =  "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate date
            ) {
        log.info("상위 문제 기반 팀 랭킹 조회 요청 - period: {}, date: {}", period, date);
        return ResponseEntity.ok(teamRankingService.getTeamRanking(period, date));

    }

}
