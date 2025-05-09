package force.ssafy.global.util;

import force.ssafy.domain.algorithm.dto.request.AlgorithmMappingRequest;
import force.ssafy.domain.problem.dto.request.ProblemCreateRequest;
import force.ssafy.domain.problem.entity.ProblemTier;
import force.ssafy.domain.solvedac.entity.DisplayName;
import force.ssafy.domain.solvedac.entity.ProblemItem;
import force.ssafy.domain.solvedac.entity.Tag;

import java.util.Arrays;
import java.util.List;

public class ProblemConverter {

    public static List<ProblemCreateRequest> convert(List<ProblemItem> items) {
        return items.stream()
                .map(ProblemConverter::convertToProblemCreateRequest)
                .toList();
    }

    private static ProblemCreateRequest convertToProblemCreateRequest(ProblemItem item) {
        return ProblemCreateRequest.of(
                item.getProblemId(),
                item.getTitleKo(),
                convertLevelToTier(item.getLevel()),
                "https://www.acmicpc.net/problem/" + item.getProblemId(),
                item.getTags().stream()
                        .map(tag -> new AlgorithmMappingRequest(getKoreanTagName(tag)))
                        .toList()
        );
    }

    private static ProblemTier convertLevelToTier(int level) {
        return Arrays.stream(ProblemTier.values())
                .filter(t -> t.getPoint() == level)
                .findFirst()
                .orElse(ProblemTier.UNRATED);
    }

    private static String getKoreanTagName(Tag tag) {
        return tag.getDisplayNames().stream()
                .filter(d -> "ko".equals(d.getLanguage()))
                .map(DisplayName::getName)
                .findFirst()
                .orElse("기타");
    }
}
