package force.ssafy.domain.algorithm.dto.request;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class AlgorithmMappingRequest {
    private String name;
}
