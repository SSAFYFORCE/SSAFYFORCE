package force.ssafy.domain.problem.entity;

import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Problem {

    @Id @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long problemNumber;
    @Column(nullable = false)
    private String title;

    // Enum 값을 문자열로 저장(EnumType.ORDINAL 은 enum 클래스의 순서가 바뀌었을 경우 치명적)
    @Enumerated(EnumType.STRING)
    private ProblemTier tier = ProblemTier.UNRATED; // 기본값 직접 설정
    private String url;

    @OneToMany(mappedBy = "problem",cascade = CascadeType.ALL)
    private List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
}
