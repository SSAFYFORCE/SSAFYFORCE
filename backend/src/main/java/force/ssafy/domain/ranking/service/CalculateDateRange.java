package force.ssafy.domain.ranking.service;

import force.ssafy.domain.ranking.controller.dto.RankingPeriod;
import force.ssafy.domain.ranking.service.dto.DateRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

class CalculateDateRange {
    private CalculateDateRange() {
    }

    protected static DateRange calculateDateRange(RankingPeriod period, LocalDate date) {
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
}
