package force.ssafy.domain.solvedac.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SolvedAcUserInfo {
    private String tier;
    private int rating;
    private int solvedCount;
    private double correctRate;
}