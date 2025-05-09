package force.ssafy.domain.solvedac.entity;

import lombok.Getter;
import java.util.List;

@Getter
public class Tag {
    private String key;
    private List<DisplayName> displayNames;
}
