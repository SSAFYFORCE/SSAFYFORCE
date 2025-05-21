package force.ssafy.domain.problem.service;

import force.ssafy.domain.problem.dto.request.ProblemCreateRequest;
import force.ssafy.domain.problem.repository.ProblemRepository;
import force.ssafy.domain.solvedac.entity.ProblemItem;
import force.ssafy.domain.solvedac.service.SolvedAcService;
import force.ssafy.global.util.ProblemConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemCrawlService {

    private final ProblemRepository problemRepository;
    private final SolvedAcService solvedAcService;
    private final ProblemService problemService;

    @Transactional
    public void addProblem(Long end) {

        long start = Optional.ofNullable(problemRepository.findMaxProblemNumber())
                .orElse(999L) + 1;
        int batchSize = 100;

        log.info("start add problem number from {} to {}", start, end);

        for (long batchStart = start; batchStart <= end; batchStart += batchSize) {
            // 1) batchSize 개씩, end 를 넘지 않도록 끊기 (inclusive)
            long batchEnd = Math.min(batchStart + batchSize - 1, end);

            // 처리 건수 = batchEnd - batchStart + 1
            int count = (int)(batchEnd - batchStart + 1);
            log.info("{} 부터 {} 까지 {} 건 처리 중", batchStart, batchEnd, count);

            List<Long> problemIds = new ArrayList<>();
            // 2) i <= batchEnd 로 바꿔서 정확히 포함
            for (long i = batchStart; i <= batchEnd; i++) {
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
