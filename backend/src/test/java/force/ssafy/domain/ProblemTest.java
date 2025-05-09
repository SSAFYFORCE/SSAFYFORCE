package force.ssafy.domain;

import force.ssafy.domain.problem.dto.request.ProblemCreateRequest;
import force.ssafy.domain.problem.service.ProblemService;
import force.ssafy.domain.solvedac.entity.ProblemItem;
import force.ssafy.domain.solvedac.service.SolvedAcService;
import force.ssafy.global.util.ProblemConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ProblemTest {

    @Autowired
    ProblemService problemService;

    @Autowired
    SolvedAcService solvedAcService;

    @Test
    @DisplayName("start 번호부터 end-1 번호까지 problem 크롤링해오는 코드. solved ac api에서 최대 100개씩 호출 가능")
    void setup() {
        long start = 33882;
        long end = 33883;
        int batchSize = 100;

        for (long batchStart = start; batchStart < end; batchStart += batchSize) {
            long batchEnd = Math.min(batchStart + batchSize, end);

            List<Long> problemIds = new ArrayList<>();
            for (long i = batchStart; i < batchEnd; i++) {
                System.out.println(i);
                problemIds.add(i);
            }

            List<ProblemItem> items = solvedAcService.getProblems(problemIds);
            List<ProblemCreateRequest> requests = ProblemConverter.convert(items);

            for (ProblemCreateRequest req : requests) {
                problemService.save(req);
            }
        }
    }

}
