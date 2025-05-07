package force.ssafy.domain.problemAlgorithm.entity;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.problem.entity.Problem;
import jakarta.persistence.*;

@Entity
public class ProblemAlgorithm {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;
}
