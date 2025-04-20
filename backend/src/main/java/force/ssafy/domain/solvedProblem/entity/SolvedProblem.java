package force.ssafy.domain.solvedProblem.entity;

import force.ssafy.domain.problem.entity.Problem;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class SolvedProblem {

    @Id @GeneratedValue
    private long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="member_id")
//    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="problem_id", nullable = false)
    private Problem problem;
    private LocalDateTime solvedDate;
    private LanguageType language;
    private int timeComplexity;
    private int spaceComplexity;
    private String submitUrl;
}
