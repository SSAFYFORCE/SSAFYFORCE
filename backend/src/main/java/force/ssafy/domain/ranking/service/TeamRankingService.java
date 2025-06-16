package force.ssafy.domain.ranking.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.controller.dto.response.TeamRankingResponse;
import force.ssafy.domain.ranking.service.dto.DateRange;
import force.ssafy.domain.ranking.service.dto.TeamRankingDto;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamMember.entity.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamRankingService {
    private final SolvedProblemRepository solvedProblemRepository;
    private final TeamRepository teamRepository;

    // 팀 제약조건
    private static final int MIN_TEAM_SIZE = 4; // 최소 4인 이상 팀만 랭킹화

    // 점수 가중치
    private static final double PARTICIPATION_WEIGHT = 0.2;  // 참여율 20%
    private static final double PERFORMANCE_WEIGHT = 0.8;    // 성과 80%

    // 기간별 상위 문제 개수 (개인당)
    private static final Map<RankingPeriod, Integer> PROBLEMS_PER_PERSON = Map.of(
            RankingPeriod.DAILY, 1,
            RankingPeriod.WEEKLY, 3,
            RankingPeriod.MONTHLY, 10);

    public TeamRankingResponse getTeamRanking(RankingPeriod period, LocalDate date) {
        DateRange dateRange = CalculateDateRange.calculateDateRange(period, date);

        log.info("상위 문제 기반 팀 랭킹 계산 - 기간: {}, 개인당 문제수: {}, 시작: {}, 종료: {}", period, PROBLEMS_PER_PERSON.get(period), dateRange.startDate(), dateRange.endDate());

        List<TeamRankingDto> teamRankings = calculateRanking(dateRange.startDate(), dateRange.endDate(), period);

        return TeamRankingResponse.of(teamRankings, dateRange.startDate(), dateRange.endDate(), period);
    }

    private List<TeamRankingDto> calculateRanking(LocalDateTime startDate, LocalDateTime endDate, RankingPeriod period) {
        // 해당 기간 동안 해결된 문제들 중 첫 해결 문제만 조회
        List<SolvedProblem> solvedProblems = solvedProblemRepository.findBySolvedDateBetweenAndIsFirstSolved(startDate, endDate, true);

        // 유효한 팀 목록 조회
        List<Team> validTeams = getValidTeams();

        // 팀별 통계 집계(상위 문제 기반)
        Map<Team, TeamStats> teamStatsMap = aggregateTeamStats(solvedProblems, validTeams, period);

        return calculateRankingFromStats(teamStatsMap);


    }

    private List<Team> getValidTeams() {
        return teamRepository.findByDeletedFalse().stream().filter(team -> {
            int totalMembers = team.getTeamMembers().size();
            if (totalMembers < MIN_TEAM_SIZE) {
                log.debug("팀 크기 부족으로 제외: {} ({}명)", team.getName(), totalMembers);
                return false;
            }
            return true;
        }).toList();
    }

    private Map<Team, TeamStats> aggregateTeamStats(List<SolvedProblem> solvedProblems, List<Team> validTeams, RankingPeriod period) {
        Map<Team, TeamStats> teamStats = new HashMap<>();
        int problemsPerPerson = PROBLEMS_PER_PERSON.get(period);

        // 팀별로 해결한 문제 수집
        Map<Team, List<SolvedProblem>> teamProblemsMap = new HashMap<>();

        for (Team team : validTeams) {
            teamProblemsMap.put(team, new ArrayList<>());
        }
        for (SolvedProblem solvedProblem : solvedProblems) {
            Member member = solvedProblem.getMember();
            for (TeamMember teamMember : member.getTeamMembers()) {
                Team team = teamMember.getTeam();

                // 랭킹 계산을 해야하는 팀에 해결한 문제 추가
                if (teamProblemsMap.containsKey(team)) {
                    teamProblemsMap.get(team).add(solvedProblem);
                }
            }
        }
        for (Map.Entry<Team, List<SolvedProblem>> entry : teamProblemsMap.entrySet()) {
            Team team = entry.getKey();
            List<SolvedProblem> teamProblems = entry.getValue();

            TeamStats stats = calcuateTeamStats(team, teamProblems, problemsPerPerson);
            teamStats.put(team, stats);

            log.debug("팀: {}, 전체문제: {}개, 상위문제: {}개, 평균점수: {:.2f}",
                    team.getName(), teamProblems.size(), stats.topProblemsCount(), stats.averageScore());
        }
        return teamStats;
    }

    private TeamStats calcuateTeamStats(Team team, List<SolvedProblem> teamProblems, int problemsPerPerson) {
        int totalMembers = team.getTeamMembers().size();

        // 활동한 팀원만 추출
        Set<Long> uniqueSolvers = teamProblems.stream()
                .map(sp -> sp.getMember().getId())
                .collect(Collectors.toSet());

        // 전체 문제 점수 합계
        int totalScore = teamProblems.stream()
                .mapToInt(sp -> sp.getProblem().getTier().getPoint())
                .sum();

        // 상위 n(maxProblems)개 문제 선별
        int maxProblems = problemsPerPerson * totalMembers;
        List<Integer> allScores = teamProblems.stream()
                .map(sp -> sp.getProblem().getTier().getPoint())
                .sorted(Comparator.reverseOrder())
                .toList();

        // 상위 문제들의 점수 합계
        int topProblemsScore = allScores.stream()
                .limit(maxProblems)
                .mapToInt(Integer::intValue)
                .sum();

        int topProblemsCount = allScores.size();

        // 평균 점수 계산(상위 문제 기반)
        double averageScore = (double) topProblemsScore / totalMembers;

        return new TeamStats(
                totalScore,
                teamProblems.size(),
                uniqueSolvers,
                topProblemsScore,
                topProblemsCount,
                averageScore
        );
    }

    private double calculateCompositeScore(Team team, TeamStats stats) {
        int totalMembers = team.getTeamMembers().size();
        int activeMembers = stats.uniqueSolvers().size();

        // 1. 참여율 점수 (0~100점)
        double participationRate = (double) activeMembers / totalMembers;
        double participationScore = participationRate * 100;

        // 2. 상위 문제 기반 성과 점수 (개인당 평균)
        double performanceScore = stats.averageScore();

        // 3. 복합 점수 계산
        double compositeScore = (participationScore * PARTICIPATION_WEIGHT) +
                (performanceScore * PERFORMANCE_WEIGHT);

        log.debug(String.format("팀: %s, 참여율: %.1f%%, 개인평균: %.2f점, 복합점수: %.2f",
                team.getName(), participationRate * 100, performanceScore, compositeScore));
        return compositeScore;
    }

    private List<TeamRankingDto> calculateRankingFromStats(Map<Team, TeamStats> teamStatsMap) {
        // 팀별 복합 점수 계산
        List<TeamWithScore> teamsWithScore = new ArrayList<>();
        for (Map.Entry<Team, TeamStats> entry : teamStatsMap.entrySet()) {
            Team team = entry.getKey();
            TeamStats teamStats = entry.getValue();
            double compositeScore = calculateCompositeScore(team, teamStats);
            teamsWithScore.add(new TeamWithScore(team, teamStats, compositeScore));
        }

        // 정렬 : 복합 점수 높은 순서
        teamsWithScore.sort((o1, o2) -> Double.compare(o2.compositeScore(), o1.compositeScore()));

        return buildRankingList(teamsWithScore);
    }

    private List<TeamRankingDto> buildRankingList(List<TeamWithScore> teamsWithScore) {
        List<TeamRankingDto> rankings = new ArrayList<>();
        int currentRank = 1;
        Double previousScore = null;
        int sameRankCount = 0;

        for (TeamWithScore teamWithScore : teamsWithScore) {
            Team team = teamWithScore.team();
            TeamStats stats = teamWithScore.stats();
            double compositeScore = calculateCompositeScore(team, stats);

            int totalMembers = team.getTeamMembers().size();
            int activeMembers = stats.uniqueSolvers().size();
            double participationRate = (double) activeMembers / totalMembers;

            // 동점자 처리
            double roundedScore = Math.round(compositeScore * 100.0) / 100.0;
            if (previousScore != null && !previousScore.equals(roundedScore)) {
                currentRank += sameRankCount;
                sameRankCount = 1;
            } else {
                sameRankCount++;
            }
            previousScore = roundedScore;

            TeamRankingDto rankingDto = TeamRankingDto.of(
                    team,
                    stats.totalScore(),
                    currentRank,
                    stats.totalSolvedCount(),
                    activeMembers,
                    stats.averageScore(),
                    participationRate,
                    compositeScore,
                    stats.topProblemsScore(),
                    stats.topProblemsCount(),
                    stats.averageScore()
            );

            rankings.add(rankingDto);
        }
        log.info("상위 문제 기반 팀 랭킹 계산 완료 - 총 {}개 팀", rankings.size());
        return rankings;
    }

    private record TeamStats(int totalScore,
                             int totalSolvedCount,
                             Set<Long> uniqueSolvers,  // 활동한 팀원 ID 집합
                             int topProblemsScore,     // 상위 문제 점수 합계
                             int topProblemsCount,     // 상위 문제 개수
                             double averageScore       // 개인당 평균 점수 (상위 문제 기반)
    ) {
    }

    private record TeamWithScore(Team team, TeamStats stats, double compositeScore) {
    }
}
