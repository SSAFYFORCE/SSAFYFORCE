package force.ssafy.domain.solvedProblem.controller;

import force.ssafy.domain.solvedProblem.controller.dto.response.SolvedProblemResponse;
import force.ssafy.domain.solvedProblem.entity.SolvedProblem;
import force.ssafy.domain.solvedProblem.service.SolvedProblemService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/solvedProblems")
public class SolvedProblemController {
    private final SolvedProblemService solvedProblemService;

    @GetMapping
    public ResponseEntity<List<SolvedProblemResponse>> getAllSolvedProblems() {
        List<SolvedProblemResponse> solvedProblemResponses = new ArrayList<>();
        List<SolvedProblem> solvedProblems = solvedProblemService.findAll();
        System.out.println(solvedProblems);
        solvedProblems.forEach(solvedProblem -> {
            solvedProblemResponses.add(SolvedProblemResponse.from(solvedProblem));
        });
        return ResponseEntity.ok(solvedProblemResponses);
    }
}
