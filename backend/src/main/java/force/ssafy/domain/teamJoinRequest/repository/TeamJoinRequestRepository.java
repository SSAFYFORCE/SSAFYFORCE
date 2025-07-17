package force.ssafy.domain.teamJoinRequest.repository;

import force.ssafy.domain.teamJoinRequest.entity.JoinStatus;
import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    Optional<TeamJoinRequest> findByTeam_IdAndRequester_IdAndStatus(
            Long teamId,
            Long requesterId,
            JoinStatus status
    );

    List<TeamJoinRequest> findByTeam_IdAndStatus(Long teamId, JoinStatus status);
}
