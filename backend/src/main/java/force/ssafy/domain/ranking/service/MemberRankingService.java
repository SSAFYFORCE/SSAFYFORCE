package force.ssafy.domain.ranking.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.controller.dto.response.RankingResponse;
import force.ssafy.domain.ranking.service.dto.RankingDto;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;

import java.time.*;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberRankingService {
    private final SolvedProblemRepository solvedProblemRepository;

    public RankingResponse getRanking(RankingPeriod period, LocalDate date) {
        DateRange dateRange = calculateDateRange(period, date);

        log.info("랭킹 계산 - 기간 : {}, 시작 : {}, 종료 : {}", period, dateRange.startDate(), dateRange.endDate());

        List<RankingDto> rankings = calculateRankings(dateRange.startDate(), dateRange.endDate());

        return RankingResponse.of(
                rankings,
                dateRange.startDate(),
                dateRange.endDate(),
                period
        );
    }

    private DateRange calculateDateRange(RankingPeriod period, LocalDate date) {
        switch (period) {
            case DAILY -> {
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
                return new DateRange(startOfDay, endOfDay);
            }
            case WEEKLY -> {
                LocalDateTime startOfWeek = date.with(DayOfWeek.MONDAY).atStartOfDay();
                LocalDateTime endOfWeek = startOfWeek.plusWeeks(1).minusNanos(1);
                return new DateRange(startOfWeek, endOfWeek);
            }
            case MONTHLY -> {
                LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
                return new DateRange(startOfMonth, endOfMonth);
            }
        }
        throw new IllegalArgumentException("지원하지 않는 기간입니다: " + period);
    }

    private List<RankingDto> calculateRankings(LocalDateTime startDate, LocalDateTime endDate) {
        List<SolvedProblem> solvedProblems = solvedProblemRepository
                .findBySolvedDateBetweenAndIsFirstSolved(startDate, endDate, true);

        Map<Member, MemberStats> memberStatsMap = aggregateMemberStats(solvedProblems);
        return calculateRankingFromStats(memberStatsMap);


    }

    /**
     * 회원별 통계 집계
     */
    private Map<Member, MemberStats> aggregateMemberStats(List<SolvedProblem> solvedProblems) {
        Map<Member, MemberStats> memberStats = new HashMap<>();
        for (SolvedProblem sp : solvedProblems) {
            Member member = sp.getMember();
            int point = sp.getProblem().getTier().getPoint();

            memberStats.merge(member, new MemberStats(point, 1),
                    (existing, newStats) -> new MemberStats(
                            existing.totalScore() + newStats.totalScore(),
                            existing.SolvedCount() + newStats.SolvedCount()
                    )
            );
        }
        return memberStats;
    }

    /**
     * 통계 데이터로부터 랭킹 계산
     */
    private List<RankingDto> calculateRankingFromStats(Map<Member, MemberStats> memberStatsMap) {
        // 점수 기준 내림차순
        List<Map.Entry<Member, MemberStats>> sortedEntries = memberStatsMap.entrySet()
                .stream()
                .sorted(Comparator.comparing((Map.Entry<Member, MemberStats> entry) -> entry.getValue().totalScore()
                ).reversed())
                .toList();


        List<RankingDto> rankings = new ArrayList<>();
        int currentRank = 1;
        int previousScore = Integer.MAX_VALUE;
        int sameRankCount = 0;

        // 점수 높은 순으로 등수 부여
        for (Map.Entry<Member, MemberStats> entry : sortedEntries) {
            Member member = entry.getKey();
            MemberStats stats = entry.getValue();
            int score = stats.totalScore();

            // 동점자 처리
            if (score < previousScore) {
                currentRank += sameRankCount;
                sameRankCount = 1;
            } else {
                sameRankCount++;
            }
            previousScore = score;

            RankingDto rankingDto = RankingDto.of(
                    member,
                    score,
                    currentRank,
                    stats.SolvedCount()
            );
            rankings.add(rankingDto);
        }
        return rankings;
    }

    /**
     * 날짜 범위를 표현하는 레코드
     */
    private record DateRange(LocalDateTime startDate, LocalDateTime endDate) {
    }

    /**
     * 회원 통계를 표현하는 레코드
     */
    private record MemberStats(int totalScore, int SolvedCount) {
    }

}