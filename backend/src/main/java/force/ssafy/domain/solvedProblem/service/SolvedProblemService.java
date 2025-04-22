package force.ssafy.domain.solvedProblem.service;

import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedProblemService {

    private final SolvedProblemRepository solvedProblemRepository;

    public List<SolvedProblem> findAll() {
        return solvedProblemRepository.findAll();
    }
}
