package force.ssafy.domain.algorithm.controller;

import force.ssafy.domain.algorithm.dto.request.AlgorithmCreateRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmGetResponse;
import force.ssafy.domain.algorithm.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/algorithms")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    @PostMapping
    public ResponseEntity<Void> saveAll(@RequestBody List<AlgorithmCreateRequest> algorithms) {
        algorithmService.addAlgorithms(algorithms);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<AlgorithmGetResponse>> findAll() {
        return ResponseEntity.ok().body(algorithmService.findAll());
    }
}
