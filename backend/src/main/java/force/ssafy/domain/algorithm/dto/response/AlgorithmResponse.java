package force.ssafy.domain.algorithm.dto.response;

import force.ssafy.domain.algorithm.entity.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AlgorithmResponse {
    private long id;
    private String name;

    public static AlgorithmResponse from(Algorithm algorithm) {
        return AlgorithmResponse.builder()
                .id(algorithm.getId())
                .name(algorithm.getName())
                .build();
    }
}
