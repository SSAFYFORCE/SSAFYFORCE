package force.ssafy.domain.teamMember.repository;

import force.ssafy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamMemberRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Member> findTeamMemberById(Long teamId) {
        return em.createQuery(
                        "SELECT tm.member FROM TeamMember tm JOIN FETCH tm.member WHERE tm.team.id = :teamId", Member.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }
}
