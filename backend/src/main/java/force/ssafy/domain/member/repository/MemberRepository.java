package force.ssafy.domain.member.repository;


import force.ssafy.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByNickname(String nickname);
    boolean existsBySolvedAcId(String solvedAcId);
    Optional<Member> findBySolvedAcId(String solvedAcId);
    Page<Member> findByVerified(boolean verified, Pageable pageable); // 인증된 회원 페이징 조회 (배치 Reader용)
    @EntityGraph(attributePaths = { "teamMembers", "teamMembers.team" })
    Optional<Member> findWithTeamsBySolvedAcId(String solvedAcId);
}