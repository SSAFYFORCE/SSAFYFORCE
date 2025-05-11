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

    public RankingResponse getDailyRanking(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return RankingResponse.of(getRanking(startOfDay, endOfDay),
                startOfDay,
                endOfDay,
                RankingPeriod.DAILY);
    }

    public RankingResponse getWeeklyRanking(LocalDate date) {
        LocalDateTime startOfWeek = date.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7).minusSeconds(1);
        return RankingResponse.of(getRanking(startOfWeek, endOfWeek),
                startOfWeek,
                endOfWeek,
                RankingPeriod.WEEKLY);
    }

    public RankingResponse getMonthlyRanking(LocalDate date) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return RankingResponse.of(getRanking(startOfMonth, endOfMonth),
                startOfMonth,
                endOfMonth,
                RankingPeriod.MONTHLY);
    }

    private List<RankingDto> getRanking(LocalDateTime startDate, LocalDateTime endDate) {
        List<SolvedProblem> solvedProblems = solvedProblemRepository
                .findBySolvedDateBetweenAndIsFirstSolved(startDate, endDate, true);
        Map<Member, Integer> scoreMap = new HashMap<>();
        Map<Member, Integer> countMap = new HashMap<>();

        for (SolvedProblem sp : solvedProblems) {
            Member member = sp.getMember();
            int point = sp.getProblem().getTier().getPoint();
            scoreMap.merge(member, point, Integer::sum);
            countMap.merge(member, 1, Integer::sum);

        }
        List<Member> members = new ArrayList<>(scoreMap.keySet());
        members.sort((m1, m2) -> scoreMap.get(m2).compareTo(scoreMap.get(m1))); // 점수 내림차순

        List<RankingDto> rankings = new ArrayList<>();
        int currentRank = 1;
        int previousScore = Integer.MAX_VALUE;
        int sameRankCount = 0;

        for (Member member : members) {
            Integer score = scoreMap.get(member);

            // 동점자 처리
            if (score < previousScore) {
                currentRank += sameRankCount;
                sameRankCount = 1;
            } else {
                sameRankCount++;
            }

            RankingDto rankingDto = RankingDto.of(
                    member,
                    score,
                    currentRank,
                    countMap.get(member)
            );

            rankings.add(rankingDto);
            previousScore = score;
        }
        return rankings;
    }

}
