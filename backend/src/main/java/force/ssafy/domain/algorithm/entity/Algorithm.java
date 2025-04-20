package force.ssafy.domain.algorithm.entity;

import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Algorithm {

    @Id @GeneratedValue
    private long id;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL)
    private List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
}
