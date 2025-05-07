package force.ssafy.domain.solvedProblem.controller.dto.response;

import force.ssafy.domain.problem.entity.ProblemTier;
import force.ssafy.domain.solvedProblem.entity.LanguageType;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
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
        return SolvedProblemResponse.builder()
                .id(solvedProblem.getId())
                .memberName(solvedProblem.getMember().getName())
                .problemId(solvedProblem.getProblem().getId())
                .problemNumber(solvedProblem.getProblem().getProblemNumber())
                .problemTitle(solvedProblem.getProblem().getTitle())
                .problemTier(solvedProblem.getProblem().getTier())
                .problemUrl(solvedProblem.getProblem().getUrl())
                .solvedDate(solvedProblem.getSolvedDate())
                .language(solvedProblem.getLanguage())
                .timeComplexity(solvedProblem.getTimeComplexity())
                .spaceComplexity(solvedProblem.getSpaceComplexity())
                .submitUrl(solvedProblem.getSubmitUrl())
                .build();
    }
}