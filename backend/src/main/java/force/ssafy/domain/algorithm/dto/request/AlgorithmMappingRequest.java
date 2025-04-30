package force.ssafy.domain.algorithm.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlgorithmMappingRequest {
    private String name;
}
