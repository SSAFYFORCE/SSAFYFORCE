package force.ssafy.domain.auth.service;

import force.ssafy.domain.auth.dto.request.SignInDto;
import force.ssafy.domain.auth.dto.request.SignUpDto;
import force.ssafy.domain.auth.dto.response.TokenDto;
import force.ssafy.domain.auth.exception.AuthError;
import force.ssafy.domain.auth.exception.AuthenticationException;
import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.exception.DuplicateSolvedAcIdException;
import force.ssafy.domain.member.exception.MemberNotFoundException;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.solvedProblem.service.SolvedProblemSyncService;
import force.ssafy.domain.solvedac.entity.VerificationCode;
import force.ssafy.domain.solvedac.repository.VerificationCodeRepository;
import force.ssafy.domain.solvedac.service.SolvedAcService;
import force.ssafy.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SolvedAcService solvedAcService;
    private final VerificationCodeRepository verificationCodeRepository;
    /**
     * 회원가입 처리
     */
    @Transactional
    public Member signUp(SignUpDto signUpDto) {
        // 닉네임 중복 확인
        if (memberRepository.existsBySolvedAcId(signUpDto.getSolvedAcId())) {
            throw new DuplicateSolvedAcIdException(signUpDto.getSolvedAcId());
        }

        // solved.ac 인증 확인
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(signUpDto.getSolvedAcId());
        if (verificationCode.isEmpty()) {
            throw new AuthenticationException("solved.ac 인증이 필요합니다. 먼저 인증을 완료해주세요.");
        }

        // 인증 코드 만료 확인
        if (verificationCode.get().isExpired()) {
            solvedAcService.deleteVerificationCode(signUpDto.getSolvedAcId());
            throw new AuthenticationException("인증이 만료되었습니다. 다시 인증을 진행해주세요.");
        }

        // 암호화 키 생성
        String encryptionKey = generateEncryptionKey();

        // 회원 엔티티 생성 및 저장
        Member member = Member.builder()
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .encryptionKey(encryptionKey)
                .solvedAcId(signUpDto.getSolvedAcId())
                .lastProblemSyncTime(LocalDateTime.of(2010, 3, 19, 0, 0, 0))
                .build();

        member.verify();
        Member savedMember = memberRepository.save(member);

        // 회원가입 완료 후 인증 코드 삭제
        solvedAcService.deleteVerificationCode(signUpDto.getSolvedAcId());
        return savedMember;
    }

    /**
     * 로그인 처리
     */
    @Transactional(readOnly = true)
    public TokenDto signIn(SignInDto signInDto) {
        // 회원 조회
        Member member = memberRepository.findBySolvedAcId(signInDto.getSolvedAcId())
                .orElseThrow(() -> new AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(signInDto.getPassword(), member.getPassword())) {
            throw new AuthenticationException(AuthError.INVALID_USERNAME_AND_PASSWORD);
        }

        // 인증 확인
        if (!member.isVerified()) {
            throw new AuthenticationException("계정 인증이 완료되지 않았습니다.");
        }

        // 토큰 생성
        return jwtTokenProvider.createToken(member.getId(), member.getSolvedAcId());
    }

    /**
     * 로그아웃 처리
     */
    public void signOut(String accessToken) {
        jwtTokenProvider.invalidateToken(accessToken);
    }

    /**
     * 암호화 키 생성
     */
    private String generateEncryptionKey() {
        byte[] randomBytes = new byte[32]; // 256 bit
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰 발급
     */
    @Transactional
    public TokenDto refreshToken(String refreshToken) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthenticationException("토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        // 2. 리프레시 토큰에서 회원 ID 추출
        Long memberId;
        try {
            memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        } catch (Exception e) {
            throw new AuthenticationException("리프레시 토큰이 잘못되었습니다.");
        }

        // 3. 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        // 4. 새 토큰 발급
        return jwtTokenProvider.createToken(member.getId(), member.getSolvedAcId());
    }
}