package force.ssafy.domain.solvedProblem.batch;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public record SyncResult(
        Long memberId,
        String memberName,
        String solvedAcId,
        Integer syncCount,
        String status,
        String errorMessage,
        LocalDateTime syncTime
) {
}
