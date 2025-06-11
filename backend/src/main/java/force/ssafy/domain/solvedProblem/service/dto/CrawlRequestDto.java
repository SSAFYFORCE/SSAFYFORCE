package force.ssafy.domain.solvedProblem.service.dto;

import force.ssafy.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CrawlRequestDto(
        String solvedAcId,
        LocalDateTime lastSyncTime
) {
    public static CrawlRequestDto of(Member member, LocalDateTime lastSyncTime) {
        return CrawlRequestDto.builder()
                .solvedAcId(member.getSolvedAcId())
                .lastSyncTime(lastSyncTime)
                .build();
    }
}
