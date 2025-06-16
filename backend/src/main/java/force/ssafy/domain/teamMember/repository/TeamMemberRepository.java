package force.ssafy.domain.teamMember.repository;

import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamMemberRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TeamMemberDto> findTeamMemberDtoByTeamId(Long teamId) {
        return em.createQuery(
                        "SELECT new force.ssafy.domain.teamMember.dto.TeamMemberDto(m.id, m.solvedAcId, m.name, m.profileImage) " +
                                "FROM TeamMember tm " +
                                "JOIN tm.member m " +
                                "WHERE tm.team.id = :teamId",
                        TeamMemberDto.class
                )
                .setParameter("teamId", teamId)
                .getResultList();
    }
}
