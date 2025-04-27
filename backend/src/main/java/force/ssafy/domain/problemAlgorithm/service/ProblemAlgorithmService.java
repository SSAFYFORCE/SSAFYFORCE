package force.ssafy.domain.problemAlgorithm.service;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problemAlgorithm.entity.ProblemAlgorithm;
import force.ssafy.domain.problemAlgorithm.repository.ProblemAlgorithmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProblemAlgorithmService {

    private final ProblemAlgorithmRepository problemAlgorithmRepository;

    public void save(Problem problem, Algorithm algorithm) {
        log.info("save 호출");
        ProblemAlgorithm newProblemAlgorithm = ProblemAlgorithm.builder().problem(problem).algorithm(algorithm).build();
        problemAlgorithmRepository.save(newProblemAlgorithm);
    }

    public void saveAll(List<ProblemAlgorithm> problemAlgorithms){
        log.info("saveAll 호출");
        problemAlgorithmRepository.saveAll(problemAlgorithms);
    }

    public List<ProblemAlgorithm> findEntityByProblemId(Long problemId){
        log.info("findEntityByProblemId 호출");
        return problemAlgorithmRepository.findByProblem_Id(problemId);
    }
}
