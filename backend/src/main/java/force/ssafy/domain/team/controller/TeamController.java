package force.ssafy.domain.team.controller;

import force.ssafy.domain.team.dto.response.TeamResponse;
import force.ssafy.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public ResponseEntity<TeamResponse> teamDetail(@PathVariable("teamId") Long teamId) {
        return ResponseEntity.ok().body(teamService.findTeamDetail(teamId));
    }
}
