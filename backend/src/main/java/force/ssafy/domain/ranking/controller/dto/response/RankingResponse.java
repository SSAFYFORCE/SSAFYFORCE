package force.ssafy.domain.ranking.controller.dto.response;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.service.dto.MemberRankingDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RankingResponse(
        LocalDateTime startDate,
        LocalDateTime endDate,
        RankingPeriod period,
        Integer totalParticipants,
        List<MemberRankingDto> rankings
) {
    public static RankingResponse of(List<MemberRankingDto> rankings,
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
