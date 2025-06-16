package force.ssafy.domain.ranking.service.dto;

import java.time.LocalDateTime;

/**
 * 날짜 범위를 표현하는 레코드
 */
public record DateRange(LocalDateTime startDate, LocalDateTime endDate) {
}
