package force.ssafy.domain.problem.dto.response;

import force.ssafy.domain.algorithm.dto.response.AlgorithmResponse;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.entity.ProblemTier;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProblemResponse {

    private long problemNumber;
    private String title;
    private ProblemTier tier;
    private String url;
    private List<AlgorithmResponse> algorithms;

    public static ProblemResponse from(Problem problem, List<AlgorithmResponse> algorithmResponses) {
        return ProblemResponse.builder()
                .problemNumber(problem.getProblemNumber())
                .title(problem.getTitle())
                .tier(problem.getTier())
                .url(problem.getUrl())
                .algorithms(algorithmResponses)
                .build();
    }
}
