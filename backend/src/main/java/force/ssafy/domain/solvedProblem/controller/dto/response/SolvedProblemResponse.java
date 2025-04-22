package force.ssafy.domain.solvedProblem.controller.dto.response;

import force.ssafy.domain.problem.entity.ProblemTier;
import force.ssafy.domain.solvedProblem.entity.LanguageType;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import java.time.LocalDateTime;

public record SolvedProblemResponse(
        long id,
        String memberName,
        long problemId,
        long problemNumber,
        String problemTitle,
        ProblemTier problemTier,
        String problemUrl,
        LocalDateTime solvedDate,
        LanguageType language,
        int timeComplexity,
        int spaceComplexity,
        String submitUrl
) {
    public static SolvedProblemResponse from(SolvedProblem solvedProblem) {
        return new SolvedProblemResponse(
                solvedProblem.getId(),
                solvedProblem.getMember().getName(),
                solvedProblem.getProblem().getId(),
                solvedProblem.getProblem().getProblemNumber(),
                solvedProblem.getProblem().getTitle(),
                solvedProblem.getProblem().getTier(),
                solvedProblem.getProblem().getUrl(),
                solvedProblem.getSolvedDate(),
                solvedProblem.getLanguage(),
                solvedProblem.getTimeComplexity(),
                solvedProblem.getSpaceComplexity(),
                solvedProblem.getSubmitUrl()
        );
    }


}
