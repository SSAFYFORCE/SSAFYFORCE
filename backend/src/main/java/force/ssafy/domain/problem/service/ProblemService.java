package force.ssafy.domain.problem.service;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.algorithm.dto.request.AlgorithmMappingRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmGetResponse;
import force.ssafy.domain.algorithm.repository.AlgorithmRepository;
import force.ssafy.domain.problem.dto.request.ProblemCreateRequest;
import force.ssafy.domain.problem.dto.response.ProblemGetResponse;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.repository.ProblemRepository;
import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import force.ssafy.domain.problemAlgorithm.repository.ProblemAlgorithmRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemAlgorithmRepository problemAlgorithmRepository;
    private final AlgorithmRepository algorithmRepository;

    @Transactional(readOnly = true)
    public List<ProblemGetResponse> findAll() {
        log.info("selectAllProblems 호출");

        List<ProblemGetResponse> problemGetResponses = new ArrayList<>();

        List<Problem> problems = problemRepository.findAll();
        for (Problem p : problems) {
            List<AlgorithmGetResponse> algorithmGetResponses = extractAlgorithmResponseFromEntity(p.getProblemAlgorithms());
            problemGetResponses.add(ProblemGetResponse.of(p, algorithmGetResponses));
        }

        return problemGetResponses;
    }

    public void save(ProblemCreateRequest problemCreateRequest) {
        log.info("save 호출");

        log.info("문제 DB에 저장");
        Problem problem = problemCreateRequest.toEntity();
        problemRepository.save(problem);

        log.info("문제와 알고리즘 매핑");
        List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
        for (AlgorithmMappingRequest algorithmMappingRequest : problemCreateRequest.getAlgorithmMappingRequests()) {
            Algorithm algorithm = algorithmRepository.findByName(algorithmMappingRequest.getName());

            ProblemAlgorithm problemAlgorithm = ProblemAlgorithm.builder()
                    .problem(problem)
                    .algorithm(algorithm)
                    .build();

            problemAlgorithms.add(problemAlgorithm);
        }

        problemAlgorithmRepository.saveAll(problemAlgorithms);
    }

    @Transactional(readOnly = true)
    public ProblemGetResponse findByProblemId(Long problemId) {
        log.info("findByProblemId 호출");

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 문제가 없습니다."));

        List<AlgorithmGetResponse> algorithmGetResponses = problemAlgorithmRepository.findByProblemId(problemId).stream()
                .map(pa -> AlgorithmGetResponse.from(pa.getAlgorithm()))
                .toList();

        return ProblemGetResponse.of(problem, algorithmGetResponses);
    }

    private List<AlgorithmGetResponse> extractAlgorithmResponseFromEntity(List<ProblemAlgorithm> problemAlgorithms) {
        log.info("extractAlgorithmResponseFromEntity 호출");

        return problemAlgorithms.stream()
                .map(pa -> AlgorithmGetResponse.from(pa.getAlgorithm()))
                .toList();
    }

}
