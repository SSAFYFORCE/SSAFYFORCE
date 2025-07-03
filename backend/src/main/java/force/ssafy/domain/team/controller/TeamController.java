package force.ssafy.domain.team.controller;

import force.ssafy.domain.team.dto.request.TeamCreateRequest;
import force.ssafy.domain.team.dto.response.TeamListResponse;
import force.ssafy.domain.team.dto.response.TeamResponse;
import force.ssafy.domain.team.service.TeamService;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 팀 상세 정보 조회
     * @param teamId
     * @return TeamResponse
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> teamDetail(@PathVariable Long teamId) {
        return ResponseEntity.ok().body(teamService.findTeamDetail(teamId));
    }

    /**
     * 모든 팀 정보 조회
     * @return TeamListResponse
     */
    @GetMapping
    public ResponseEntity<TeamListResponse> teamList() {
        return ResponseEntity.ok().body(teamService.findAllTeams());
    }

    /**
     * 팀 생성
     * @param teamCreateRequest
     */
    @PostMapping
    public ResponseEntity<Void> save(
            @RequestBody TeamCreateRequest teamCreateRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        teamService.save(teamCreateRequest, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 팀 가입
     * POST /api/v1/teams/{teamId}/join
     */
    @PostMapping("/{teamId}/join")
    public ResponseEntity<Void> joinTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails  // 스프링 시큐리티에서 꺼내는 현재 로그인 유저
    ) {

        Long memberId = userDetails.getMemberId();
        teamService.joinTeam(memberId, teamId);
        return ResponseEntity.ok().build();
    }
}
