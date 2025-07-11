package force.ssafy.domain.teamMember.repository;

import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import force.ssafy.domain.teamMember.entity.TeamMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public List<TeamMemberDto> findPreviewMemberByTeamId(Long teamId) {
        return em.createQuery(
                        "SELECT new force.ssafy.domain.teamMember.dto.TeamMemberDto(m.id, m.solvedAcId, m.name, m.profileImage) " +
                                "FROM TeamMember tm " +
                                "JOIN tm.member m " +
                                "WHERE tm.team.id = :teamId " +
                                "ORDER BY m.id ASC"
                        , TeamMemberDto.class)
                .setParameter("teamId", teamId)
                .setMaxResults(3)
                .getResultList();
    }

    @Transactional
    public void save(TeamMember tm) {
        em.persist(tm);
    }

    public boolean existsByTeamIdAndMemberId(Long teamId, Long memberId) {
        Long cnt = em.createQuery(
                        "SELECT COUNT(tm) FROM TeamMember tm " +
                                "WHERE tm.team.id = :teamId AND tm.member.id = :memberId", Long.class)
                .setParameter("teamId", teamId)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return cnt > 0;
    }
    public List<TeamMember> findByMember_Id(Long memberId) {
        return em.createQuery(
                        "SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.member.id = :memberId",
                        TeamMember.class
                )
                .setParameter("memberId", memberId)
                .getResultList();
    }

}
