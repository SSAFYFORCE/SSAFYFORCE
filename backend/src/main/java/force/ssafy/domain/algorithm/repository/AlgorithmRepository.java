package force.ssafy.domain.algorithm.repository;

import force.ssafy.domain.algorithm.entity.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
    Algorithm findByName(String name);
}
