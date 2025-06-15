package force.ssafy.domain.ranking.controller.dto.response;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.service.dto.TeamRankingDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TeamRankingResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        RankingPeriod period,
        Integer totalTeams,
        List<TeamRankingDto> rankings
) {
    public static TeamRankingResponse of(List<TeamRankingDto> rankings,
                                         LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         RankingPeriod period) {
        return TeamRankingResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .totalTeams(rankings.size())
                .rankings(rankings)
                .build();
    }
}