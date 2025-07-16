package force.ssafy.domain.teamJoinRequest.controller;

import force.ssafy.domain.teamJoinRequest.dto.TeamJoinRequestDto;
import force.ssafy.domain.teamJoinRequest.service.TeamJoinRequestService;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamJoinRequestController {

    private final TeamJoinRequestService service;

    /**
     * 팀 가입을 신청하는 API
     * @param teamId
     * @param user
     * @return
     */
    @PostMapping("/{teamId}/join-requests")
    public ResponseEntity<Void> create(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails user) {
        service.requestJoin(teamId, user.getMemberId());
        return ResponseEntity.ok().build();
    }

    // 팀별 요청 목록 (리더 전용)
    @GetMapping("/{teamId}/join-requests")
    public ResponseEntity<List<TeamJoinRequestDto>> list(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(service.getRequests(teamId, user.getMemberId()));
    }

    // 단건 조회
    @GetMapping("/{teamId}/join-requests/{reqId}")
    public ResponseEntity<TeamJoinRequestDto> get(
            @PathVariable Long teamId,
            @PathVariable Long reqId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(service.getRequest(teamId, reqId, user.getMemberId()));
    }

    // 승인
    @PatchMapping("/{teamId}/join-requests/{reqId}/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long teamId,
            @PathVariable Long reqId,
            @AuthenticationPrincipal CustomUserDetails user) {

        service.approve(teamId, reqId, user.getMemberId());
        return ResponseEntity.noContent().build();
    }

    // 거절
    @PatchMapping("/{teamId}/join-requests/{reqId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long teamId,
            @PathVariable Long reqId,
            @AuthenticationPrincipal CustomUserDetails user) {

        service.reject(teamId, reqId, user.getMemberId());
        return ResponseEntity.noContent().build();
    }
}

