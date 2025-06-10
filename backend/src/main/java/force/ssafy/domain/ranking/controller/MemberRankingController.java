package force.ssafy.domain.ranking.controller;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
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

    @GetMapping
    public ResponseEntity<RankingResponse> getMemberRanking(
            @RequestParam(defaultValue = "DAILY") RankingPeriod period,
            //@RequestParam의 defaultValue는 문자열을 넣어줘하기 때문에 Spring Expression Language 방식으로 현재시간을 기본값으로 설정
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(memberRankingService.getRanking(period, date));
    }

}
