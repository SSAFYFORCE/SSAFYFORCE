package force.ssafy.domain.ranking.service.dto;

import force.ssafy.domain.team.entity.Team;
import lombok.Builder;
@Builder
public record TeamRankingDto(
        Long teamId,
        String teamName,
        String teamDescription,
        String profileImage,
        Integer totalScore,                 // 전체 문제 점수 합계
        Integer rank,
        Integer totalSolvedCount,           // 전체 해결 문제 수
        Integer activeMemberCount,          // 활동한 팀원 수
        Integer teamMemberCount,            // 전체 팀원 수
        Double averageScore,                // 상위 문제 기반 개인당 평균 (실제 랭킹 기준)
        Double participationRate,           // 참여율 (0.0~1.0)
        Double compositeScore,              // 최종 복합 점수
        Integer topProblemsScore,           // 상위 문제 점수 합계
        Integer topProblemsCount,           // 상위 문제 개수
        Double topProblemsAverage           // 상위 문제 기반 개인당 평균 (averageScore와 동일)
) {
    public static TeamRankingDto of(Team team, Integer totalScore, Integer rank,
                                    Integer totalSolvedCount, Integer activeMemberCount,
                                    Double averageScore, Double participationRate,
                                    Double compositeScore, Integer topProblemsScore,
                                    Integer topProblemsCount, Double topProblemsAverage) {
        return TeamRankingDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .teamDescription(team.getDescription())
                .profileImage(team.getProfileImage())
                .totalScore(totalScore)
                .rank(rank)
                .totalSolvedCount(totalSolvedCount)
                .activeMemberCount(activeMemberCount)
                .teamMemberCount(team.getTeamMembers().size())
                .averageScore(averageScore)
                .participationRate(participationRate)
                .compositeScore(compositeScore)
                .topProblemsScore(topProblemsScore)
                .topProblemsCount(topProblemsCount)
                .topProblemsAverage(topProblemsAverage)
                .build();
    }
}