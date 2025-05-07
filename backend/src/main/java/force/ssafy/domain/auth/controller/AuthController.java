package force.ssafy.domain.auth.controller;


import force.ssafy.domain.auth.dto.request.RefreshTokenRequestDto;
import force.ssafy.domain.auth.dto.request.SignInDto;
import force.ssafy.domain.auth.dto.request.SignUpDto;
import force.ssafy.domain.auth.dto.response.TokenDto;
import force.ssafy.domain.auth.exception.AuthenticationException;
import force.ssafy.domain.auth.service.AuthService;
import force.ssafy.domain.member.dto.response.MemberDto;
import force.ssafy.domain.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     */
    @PostMapping("/sign-in")
    public ResponseEntity<TokenDto> signIn(@Valid @RequestBody SignInDto signInDto) {
        TokenDto tokenDto = authService.signIn(signInDto);
        return ResponseEntity.ok(tokenDto);
    }

    /**
     * 회원가입 API
     */
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDto> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        Member member = authService.signUp(signUpDto);

        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .solvedAcId(member.getSolvedAcId())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(memberDto);
    }

    /**
     * 로그아웃 API
     */
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestHeader("Authorization") String token) {
        // Bearer 제거
        String accessToken = token.replace("Bearer ", "");
        authService.signOut(accessToken);
        return ResponseEntity.ok().build();
    }

    /**
     * 토큰 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            TokenDto tokenDto = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(tokenDto);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "유효하지 않은 리프레시 토큰입니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
