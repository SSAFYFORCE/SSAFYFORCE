package force.ssafy.domain.algorithm.controller;

import force.ssafy.domain.algorithm.dto.request.AlgorithmRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmResponse;
import force.ssafy.domain.algorithm.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/algorithm")
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    @PostMapping(value = "")
    public ResponseEntity<Void> addAlgorithms(@RequestBody List<AlgorithmRequest> algorithms) {
        algorithmService.addAlgorithms(algorithms);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "")
    public ResponseEntity<List<AlgorithmResponse>> findALl(){
        return ResponseEntity.ok().body(algorithmService.findAll());
    }
}
