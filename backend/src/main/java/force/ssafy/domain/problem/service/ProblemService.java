package force.ssafy.domain.problem.service;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.algorithm.dto.request.AlgorithmMappingRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmResponse;
import force.ssafy.domain.algorithm.service.AlgorithmService;
import force.ssafy.domain.problem.dto.request.ProblemRequest;
import force.ssafy.domain.problem.dto.response.ProblemResponse;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.repository.ProblemRepository;
import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import force.ssafy.domain.problemAlgorithm.service.ProblemAlgorithmService;
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
    private final ProblemAlgorithmService problemAlgorithmService;
    private final AlgorithmService algorithmService;

    public List<ProblemResponse> selectAllProblems() {
        log.info("selectAllProblems 호출");
        List<ProblemResponse> problemResponses = new ArrayList<>();

        List<Problem> problems = problemRepository.findAll();
        for (Problem p : problems) {
            List<AlgorithmResponse> algorithmResponses = extractAlgorithmResponseFromEntity(p.getProblemAlgorithms());
            problemResponses.add(ProblemResponse.from(p, algorithmResponses));
        }

        return problemResponses;
    }

    public void save(ProblemRequest problemRequest) {
        log.info("save 호출");
        Problem problem = problemRequest.toEntity();
        problemRepository.save(problem);

        List<ProblemAlgorithm> problemAlgorithms = new ArrayList<>();
        for (AlgorithmMappingRequest algorithmMappingRequest : problemRequest.getAlgorithmMappingRequests()) {
            Algorithm algorithm = algorithmService.findByName(algorithmMappingRequest.getName());

            ProblemAlgorithm problemAlgorithm = ProblemAlgorithm.builder()
                    .problem(problem)
                    .algorithm(algorithm)
                    .build();

            problemAlgorithms.add(problemAlgorithm);
        }

        problemAlgorithmService.saveAll(problemAlgorithms);
    }


    private List<AlgorithmResponse> extractAlgorithmResponseFromEntity(List<ProblemAlgorithm> problemAlgorithms) {
        log.info("extractAlgorithmResponseFromEntity 호출");
        List<AlgorithmResponse> algorithmResponses = new ArrayList<>();

        for (ProblemAlgorithm problemAlgorithm : problemAlgorithms) {
            algorithmResponses.add(AlgorithmResponse.from(problemAlgorithm.getAlgorithm()));
        }

        return algorithmResponses;
    }

    public ProblemResponse findByProblemId(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 문제가 없습니다."));
        List<AlgorithmResponse> algorithmResponses = new ArrayList<>();

        List<ProblemAlgorithm> problemAlgorithms = problemAlgorithmService.findEntityByProblemId(problemId);
        for(ProblemAlgorithm problemAlgorithm : problemAlgorithms){
            algorithmResponses.add(AlgorithmResponse.from(problemAlgorithm.getAlgorithm()));
        }

        return ProblemResponse.from(problem, algorithmResponses);
    }

}
