package force.ssafy.domain.problem.controller;

import force.ssafy.domain.problem.dto.request.ProblemRequest;
import force.ssafy.domain.problem.dto.response.ProblemResponse;
import force.ssafy.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping(value = "")
    public ResponseEntity<List<ProblemResponse>> selectAllProblems() {
        return ResponseEntity.ok().body(problemService.selectAllProblems());
    }

    @PostMapping(value = "")
    public ResponseEntity<Void> addProblem(@RequestBody ProblemRequest problemRequest) {
        problemService.save(problemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{problemId}")
    public ResponseEntity<ProblemResponse> selectOneProblem(@PathVariable Long problemId){
        return ResponseEntity.ok().body(problemService.findByProblemId(problemId));
    }

}
