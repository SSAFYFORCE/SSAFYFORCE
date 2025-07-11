package force.ssafy.domain.teamMember.repository;

import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import force.ssafy.domain.teamMember.entity.TeamMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 각 팀의 실제 멤버 수를 조회하는 메서드
     * @param teamIds 팀 ID 목록
     * @return Map<팀ID, 멤버수>
     */
    public Map<Long, Long> findMemberCountsByTeamIds(List<Long> teamIds) {
        if (teamIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> results = em.createQuery(
                        "SELECT tm.team.id, COUNT(tm) " +
                                "FROM TeamMember tm " +
                                "WHERE tm.team.id IN :teamIds " +
                                "GROUP BY tm.team.id", Object[].class)
                .setParameter("teamIds", teamIds)
                .getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],  // teamId
                        result -> (Long) result[1]   // count
                ));
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
}