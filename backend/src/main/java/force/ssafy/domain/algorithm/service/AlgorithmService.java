package force.ssafy.domain.algorithm.service;

import force.ssafy.domain.algorithm.entity.Algorithm;
import force.ssafy.domain.algorithm.repository.AlgorithmRepository;
import force.ssafy.domain.algorithm.dto.request.AlgorithmCreateRequest;
import force.ssafy.domain.algorithm.dto.response.AlgorithmGetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlgorithmService {

    private final AlgorithmRepository algorithmRepository;

    @Transactional
    public void addAlgorithms(List<AlgorithmCreateRequest> algorithmCreateRequests) {
        log.info("addAlgorithms 호출");

        List<Algorithm> algorithms = algorithmCreateRequests.stream()
                .map(AlgorithmCreateRequest::toEntity)
                .toList();

        algorithmRepository.saveAll(algorithms);
    }

    public List<AlgorithmGetResponse> findAll() {
        log.info("findAll 호출");

        return algorithmRepository.findAll().stream()
                .map(AlgorithmGetResponse::from)
                .toList();
    }

}
