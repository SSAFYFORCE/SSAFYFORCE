package force.ssafy.domain.solvedProblem.service.dto;


import java.time.LocalDateTime;

public record SolvedProblemDto(
        Integer submissionId,
        String solvedAcId,
        Long problemNumber,
        String problemTitle,
        String language,
        Integer timeComplexity,
        Integer spaceComplexity,
        String submitUrl,
        LocalDateTime solvedDate
) {
}
