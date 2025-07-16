package force.ssafy.domain.teamJoinRequest.repository;

import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
}
