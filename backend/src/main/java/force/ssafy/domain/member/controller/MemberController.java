package force.ssafy.domain.member.controller;

import force.ssafy.domain.member.dto.request.MemberUpdateResponse;
import force.ssafy.domain.member.dto.request.PasswordChangeDto;
import force.ssafy.domain.member.dto.response.MemberDto;
import force.ssafy.domain.member.dto.response.NicknameVerificationDto;
import force.ssafy.domain.member.service.MemberService;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<MemberDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberDto memberDto = memberService.getMemberInfo(userDetails.getMemberId());
        return ResponseEntity.ok(memberDto);
    }

    /**
     * 닉네임 중복 확인 API
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameVerificationDto> checkNicknameAvailability(
            @RequestParam("nickname") String nickname) {
        NicknameVerificationDto result = memberService.checkNicknameAvailability(nickname);
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 정보 수정 API
     */
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMemberInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateResponse updateDto) {
        memberService.updateMemberInfo(userDetails.getMemberId(), updateDto);
        return ResponseEntity.ok().build();
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
     * 회원 탈퇴 API
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}