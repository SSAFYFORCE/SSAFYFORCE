package force.ssafy.domain.solvedac.entity;

import lombok.Getter;
import java.util.List;

@Getter
public class ProblemItem {
    private long problemId;
    private String titleKo;
    private int level;
    private List<Tag> tags;
}
