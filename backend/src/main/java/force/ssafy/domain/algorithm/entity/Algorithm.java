package force.ssafy.domain.algorithm.entity;

import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Algorithm {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "algorithm", cascade = CascadeType.ALL)
    private List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
}
