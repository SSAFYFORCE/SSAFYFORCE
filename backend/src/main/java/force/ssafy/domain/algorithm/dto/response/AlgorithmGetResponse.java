package force.ssafy.domain.algorithm.dto.response;

import force.ssafy.domain.algorithm.entity.Algorithm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlgorithmGetResponse {
    private long id;
    private String name;

    public static AlgorithmGetResponse from(Algorithm algorithm) {
        return AlgorithmGetResponse.builder()
                .id(algorithm.getId())
                .name(algorithm.getName())
                .build();
    }
}
