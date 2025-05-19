package force.ssafy.domain.problem.controller;

import force.ssafy.domain.problem.dto.request.ProblemCreateRequest;
import force.ssafy.domain.problem.dto.response.ProblemGetResponse;
import force.ssafy.domain.problem.service.ProblemCrawlService;
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
    private final ProblemCrawlService problemCrawlService;

    @GetMapping
    public ResponseEntity<List<ProblemGetResponse>> findAll() {
        return ResponseEntity.ok().body(problemService.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ProblemCreateRequest problemCreateRequest) {
        problemService.save(problemCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{problemId}")
    public ResponseEntity<ProblemGetResponse> findByProblemId(@PathVariable Long problemId){
        return ResponseEntity.ok().body(problemService.findByProblemId(problemId));
    }

    @PostMapping("/crawal")
    public ResponseEntity<Void> addProblemWithCrawling(@RequestParam Long end){
        problemCrawlService.addProblem(end);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
