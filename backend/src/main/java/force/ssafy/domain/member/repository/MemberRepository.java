package force.ssafy.domain.member.repository;


import force.ssafy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    Optional<Member> findBySolvedAcId(String solvedAcId);
}