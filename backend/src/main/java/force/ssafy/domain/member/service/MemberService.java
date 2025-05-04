package force.ssafy.domain.member.service;

import force.ssafy.domain.member.dto.request.MemberUpdateResponse;
import force.ssafy.domain.member.dto.request.PasswordChangeDto;
import force.ssafy.domain.member.dto.response.MemberDto;
import force.ssafy.domain.member.dto.response.NicknameVerificationDto;
import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.exception.InvalidPasswordException;
import force.ssafy.domain.member.exception.MemberNotFoundException;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(member);
    }

    public CustomUserDetails loadUserById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));

        return new CustomUserDetails(member);
    }

    /**
     * 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public MemberDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .classLevel(member.getClassLevel())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .solvedAcId(member.getSolvedAcId())
                .build();
    }

    /**
     * 닉네임 중복 확인
     */
    @Transactional(readOnly = true)
    public NicknameVerificationDto checkNicknameAvailability(String nickname) {
        boolean isAvailable = !memberRepository.existsByNickname(nickname);

        return NicknameVerificationDto.builder()
                .available(isAvailable)
                .message(isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.")
                .build();
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateResponse updateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));


        member.updateProfile(
                updateDto.getName(),
                updateDto.getClassLevel(),
                updateDto.getProfileImage()
        );

        memberRepository.save(member);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long memberId, PasswordChangeDto passwordChangeDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), member.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 설정
        member.updatePassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));

        memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}