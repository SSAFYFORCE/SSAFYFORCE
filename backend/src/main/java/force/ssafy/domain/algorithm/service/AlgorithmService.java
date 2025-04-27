package force.ssafy.domain.algorithm.service;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.algorithm.repository.AlgorithmRepository;
import force.ssafy.domain.algorithm.dto.request.AlgorithmRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmResponse;
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
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    public void addAlgorithms(List<AlgorithmRequest> algorithmRequests) {
        log.info("addAlgorithms 호출");
        List<Algorithm> algorithms = new ArrayList<>();

        for (AlgorithmRequest algorithmRequest : algorithmRequests) {
            algorithms.add(algorithmRequest.toEntity());
        }

        algorithmRepository.saveAll(algorithms);
    }

    public List<AlgorithmResponse> selectAllAlgorithms() {
        log.info("selectAllAlgorithms 호출");
        List<AlgorithmResponse> algorithmResponses = new ArrayList<>();

        List<Algorithm> algorithms = algorithmRepository.findAll();
        for (Algorithm algorithm : algorithms) {
            algorithmResponses.add(AlgorithmResponse.from(algorithm));
        }

        return algorithmResponses;
    }

    public Algorithm findByName(String name) {
        log.info("findByName 호출");
        return algorithmRepository.findByName(name);
    }

    public List<AlgorithmResponse> findAll() {
        log.info("AlgorithmResponse 호출");
        List<AlgorithmResponse> algorithmResponses = new ArrayList<>();

        List<Algorithm> algorithms = algorithmRepository.findAll();
        for(Algorithm algorithm : algorithms){
            algorithmResponses.add(AlgorithmResponse.from(algorithm));
        }

        return algorithmResponses;
    }

}
