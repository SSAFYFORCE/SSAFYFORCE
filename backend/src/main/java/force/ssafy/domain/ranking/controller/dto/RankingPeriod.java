package force.ssafy.domain.ranking.controller.dto;

import lombok.Getter;

@Getter
public enum RankingPeriod {
    DAILY("일간"),
    WEEKLY("주간"),
    MONTHLY("월간");

    private final String description;

    RankingPeriod(String description) {
        this.description = description;
    }
}
