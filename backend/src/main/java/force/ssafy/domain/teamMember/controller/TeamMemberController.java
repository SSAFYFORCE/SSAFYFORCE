package force.ssafy.domain.teamMember.controller;

import force.ssafy.domain.teamMember.service.TeamMemberService;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamMemberController {

  private final TeamMemberService teamMemberService;

  @DeleteMapping("/{teamId}/members/{memberId}")
  public ResponseEntity<Void> teamWithdraw(
      @PathVariable Long teamId,
      @PathVariable Long memberId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    teamMemberService.teamWithdraw(teamId, memberId, userDetails.getMemberId());
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{teamId}/leader/{newLeaderId}")
  public ResponseEntity<Void> teamLeaderMandate(
      @PathVariable Long teamId,
      @PathVariable Long newLeaderId,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {

    teamMemberService.teamLeaderMandate(teamId, newLeaderId,
        userDetails.getMemberId());
    return ResponseEntity.ok().build();
  }
}
