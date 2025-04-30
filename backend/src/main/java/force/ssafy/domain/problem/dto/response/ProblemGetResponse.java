package force.ssafy.domain.problem.dto.response;

import force.ssafy.domain.algorithm.dto.response.AlgorithmGetResponse;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.entity.ProblemTier;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class ProblemGetResponse {

    private long problemNumber;
    private String title;
    private ProblemTier tier;
    private String url;
    private List<AlgorithmGetResponse> algorithms;

    public static ProblemGetResponse of(Problem problem, List<AlgorithmGetResponse> algorithmGetRespons) {
        return ProblemGetResponse.builder()
                .problemNumber(problem.getProblemNumber())
                .title(problem.getTitle())
                .tier(problem.getTier())
                .url(problem.getUrl())
                .algorithms(algorithmGetRespons)
                .build();
    }
}
