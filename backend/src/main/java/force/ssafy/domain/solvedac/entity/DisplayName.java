package force.ssafy.domain.solvedac.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DisplayName {
    private String language;
    private String name;

    @JsonProperty("short")
    private String shortName;
}
