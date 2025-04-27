package force.ssafy.domain.problem.entity;

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
public class Problem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long problemNumber;
    // pr용 테스트
    @Column(nullable = false)
    private String title;

    // Enum 값을 문자열로 저장(EnumType.ORDINAL 은 enum 클래스의 순서가 바뀌었을 경우 치명적)
    @Enumerated(EnumType.STRING)
    private ProblemTier tier = ProblemTier.UNRATED; // 기본값 직접 설정
    private String url;

    @OneToMany(mappedBy = "problem",cascade = CascadeType.ALL)
    private List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
}
