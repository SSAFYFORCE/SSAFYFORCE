package force.ssafy.domain.teamMember.service;

import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.team.service.TeamService;
import force.ssafy.domain.teamMember.entity.TeamMember;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {

  private final TeamMemberRepository teamMemberRepository;
  private final TeamService teamService;  // 새로 주입

  @Transactional
  public void teamWithdraw(Long teamId, Long memberId, Long userId) {
    log.info("TeamMemberService : teamWithdraw() 호출");

    if (!memberId.equals(userId)) {
      TeamMember userTeamInfo = teamMemberRepository.findByTeamIdAndMemberId(teamId, userId);

      if (userTeamInfo == null || userTeamInfo.getRole() != MemberRole.LEADER) {
        throw new IllegalStateException("접근 권한이 없습니다.");
      }
    }

    TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId);
    if (teamMember == null) {
      throw new IllegalStateException("해당 멤버가 팀에 없습니다.");
    }

    if (teamMember.getRole() == MemberRole.LEADER) {

      long memberCount = teamMemberRepository.countByTeamId(teamId);

      if (memberCount == 1) {          // 리더 혼자라면
        teamService.deleteTeam(teamId);   // 팀 해체
        return;                           // 더 할 일 없음
      }

      throw new IllegalStateException("리더는 탈퇴할 수 없습니다. 다른 멤버에게 리더 위임 후 탈퇴하세요.");
    }
    teamMemberRepository.delete(teamMember);
  }

  @Transactional
  public void teamLeaderMandate(Long teamId, Long newLeaderId, Long userId) {
    log.info("TeamMemberService : teamLeaderMandate() 호출");

    TeamMember teamLeader = teamMemberRepository.findByTeamIdAndMemberId(teamId, userId);
    TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(teamId, newLeaderId);
    if (teamMember == null || teamLeader == null) {
      throw new IllegalStateException("해당 멤버가 팀에 없습니다.");
    }

    if (teamLeader.getRole() != MemberRole.LEADER) {
      throw new IllegalStateException("접근 권한이 없습니다.");
    }

    if (teamMember.getRole() == MemberRole.LEADER) {
      throw new IllegalStateException("이미 리더입니다.");
    }

    teamLeader.changeRoleTo(MemberRole.MEMBER);
    teamMember.changeRoleTo(MemberRole.LEADER);
  }
}
