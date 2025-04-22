package force.ssafy.domain.solvedProblem.entity;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.problem.entity.Problem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class SolvedProblem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="problem_id", nullable = false)
    private Problem problem;
    private LocalDateTime solvedDate;
    private LanguageType language;
    private int timeComplexity;
    private int spaceComplexity;
    private String submitUrl;
}
