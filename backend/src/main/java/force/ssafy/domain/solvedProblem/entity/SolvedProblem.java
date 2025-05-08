package force.ssafy.domain.solvedProblem.entity;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.problem.entity.Problem;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solved_problem", indexes = {
        @Index(name = "idx_member_problem", columnList = "member_id, problem_id"),
        @Index(name = "idx_member_solved_date", columnList = "member_id, solved_date"),
        @Index(name = "idx_solved_date", columnList = "solved_date")
})
public class SolvedProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    private LocalDateTime solvedDate;
    @Enumerated(EnumType.STRING)
    private LanguageType language;
    private int timeComplexity;
    private int spaceComplexity;
    private String submitUrl;
    private Boolean isFirstSolved;
}
