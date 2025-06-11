package force.ssafy.domain.ranking.controller.dto.response;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.service.dto.RankingDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RankingResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        RankingPeriod period,
        Integer totalParticipants,
        List<RankingDto> rankings
) {
    public static RankingResponse of(List<RankingDto> rankings,
                                     LocalDateTime startDate,
                                     LocalDateTime endDate,
                                     RankingPeriod period) {
        return RankingResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .totalParticipants(rankings.size())
                .rankings(rankings)
                .build();
    }

}
