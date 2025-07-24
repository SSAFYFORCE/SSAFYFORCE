package force.ssafy.domain.problem.spec;

import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.entity.ProblemTier;
import org.springframework.data.jpa.domain.Specification;

public class ProblemSpecs {

    public static Specification<Problem> hasProblemNumber(Long problemNumber) {
        return (root, query, cb) ->
                problemNumber == null ?
                        cb.conjunction() :
                        cb.equal(root.get("problemNumber"), problemNumber);

    }

    public static Specification<Problem> containsTitle(String title) {
        return (root, query, cb) ->
                title == null ?
                        cb.conjunction() :
                        cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Problem> sameWithTier(ProblemTier tier) {
        return (root, query, cb) ->
                tier == null ?
                        cb.conjunction() :
                        cb.equal(root.get("tier"), tier);
    }

}
