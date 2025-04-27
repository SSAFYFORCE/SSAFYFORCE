package force.ssafy.domain.algorithm.dto.request;

import force.ssafy.domain.algorithm.entity.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AlgorithmMappingRequest {
    private String name;

    public Algorithm toEntity() {
        return Algorithm.builder()
                .name(this.getName())
                .build();
    }
}
