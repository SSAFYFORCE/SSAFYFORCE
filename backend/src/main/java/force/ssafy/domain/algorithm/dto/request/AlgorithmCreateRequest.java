package force.ssafy.domain.algorithm.dto.request;

import force.ssafy.domain.algorithm.entity.Algorithm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlgorithmCreateRequest {
    private String name;

    public Algorithm toEntity() {
        return Algorithm.builder()
                .name(this.getName())
                .build();
    }
}
