package force.ssafy.domain.problem.entity;

import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Problem {

    @Id @GeneratedValue
    private long id;

    private long problem_number;
    private String title;
    private ProblemTier tier;
    private String url;

    @OneToMany(mappedBy = "problem",cascade = CascadeType.ALL)
    private List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
}
