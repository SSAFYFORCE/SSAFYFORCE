package force.ssafy.domain.problem.dto.request;

import force.ssafy.domain.algorithm.dto.request.AlgorithmMappingRequest;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.entity.ProblemTier;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@ToString
public class ProblemCreateRequest {

    private long problemNumber;
    private String title;
    private ProblemTier tier;
    private String url;
    private List<AlgorithmMappingRequest> algorithmMappingRequests;

    public Problem toEntity() {
        return Problem.builder()
                .title(this.getTitle())
                .problemNumber(this.getProblemNumber())
                .url(this.getUrl())
                .tier(this.getTier())
                .build();
    }

    public static ProblemCreateRequest of(
            long problemNumber,
            String title,
            ProblemTier tier,
            String url,
            List<AlgorithmMappingRequest> algorithmMappingRequests
    ) {
        return ProblemCreateRequest.builder()
                .problemNumber(problemNumber)
                .title(title)
                .tier(tier)
                .url(url)
                .algorithmMappingRequests(algorithmMappingRequests)
                .build();
    }

}
