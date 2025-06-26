package force.ssafy.domain.member.controller;

import force.ssafy.domain.member.dto.request.MemberUpdateRequest;
import force.ssafy.domain.member.dto.request.PasswordChangeDto;
import force.ssafy.domain.member.dto.request.PasswordResetDto;
import force.ssafy.domain.member.dto.response.*;
import force.ssafy.domain.member.service.MemberService;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;




    /**
     * 닉네임 중복 확인 API
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameVerificationDto> checkNicknameAvailability(
            @RequestParam("solvedAcId") String nickname) {
        NicknameVerificationDto result = memberService.checkNicknameAvailability(nickname);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 정보 수정 API
     */
    @PatchMapping("/me")
    public ResponseEntity<MemberUpdateResponse> updateMemberInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateRequest updateDto) {
        memberService.updateMemberInfo(userDetails.getMemberId(), updateDto);
        return ResponseEntity.ok(MemberUpdateResponse.builder()
                .message("개인정보가 성공적으로 수정되었습니다.")
                .build());
    }

    /**
     * 비밀번호 변경 API
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        memberService.changePassword(userDetails.getMemberId(), passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 재설정 API
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody PasswordResetDto passwordResetDto) {
        memberService.resetPassword(passwordResetDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴 API
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<MemberDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Accessing /me endpoint");
        log.info("UserDetails: {}", userDetails);
        log.info("MemberId: {}", userDetails != null ? userDetails.getMemberId() : "null");

        MemberDto memberDto = memberService.getMemberInfo(userDetails.getMemberId());
        return ResponseEntity.ok(memberDto);
    }

    /**
     * 회원 프로필 정보 조회 API
     */
    @GetMapping("/{solvedAcId}")
    public ResponseEntity<MemberProfileResponse> getMemberProfile(@PathVariable String solvedAcId) {
        MemberProfileResponse response = memberService.getMemberProfile(solvedAcId);
        return ResponseEntity.ok(response);
    }

    /**
     * 가입한 팀 목록 조회 API
     */
    @GetMapping("/{solvedAcId}/teams")
    public ResponseEntity<MemberTeamsResponse> getMemberTeams(
            @PathVariable String solvedAcId) {
        MemberTeamsResponse response = memberService.getMemberTeams(solvedAcId);
        return ResponseEntity.ok(response);
    }
}