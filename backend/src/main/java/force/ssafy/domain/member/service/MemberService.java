package force.ssafy.domain.member.service;

import force.ssafy.domain.awsS3.service.AwsS3Service;
import force.ssafy.domain.member.dto.request.MemberUpdateRequest;
import force.ssafy.domain.member.dto.request.PasswordChangeDto;
import force.ssafy.domain.member.dto.request.PasswordResetDto;
import force.ssafy.domain.member.dto.response.MemberDto;
import force.ssafy.domain.member.dto.response.MemberProfileResponse;
import force.ssafy.domain.member.dto.response.MemberTeamsResponse;
import force.ssafy.domain.member.dto.response.NicknameVerificationDto;
import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.exception.InvalidPasswordException;
import force.ssafy.domain.member.exception.MemberNotFoundException;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.solvedac.entity.SolvedAcUserInfo;
import force.ssafy.domain.solvedac.service.SolvedAcApiService;
import force.ssafy.domain.team.dto.response.TeamSimpleResponse;
import force.ssafy.global.error.exception.EntityNotFoundException;
import force.ssafy.global.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SolvedAcApiService solvedAcApiService;
    private final AwsS3Service awsS3Service;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findBySolvedAcId(username)
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

        // solved.ac API를 통해 실제 데이터 조회
        SolvedAcUserInfo solvedAcInfo = solvedAcApiService.getUserInfo(member.getSolvedAcId());

        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .profileImage(member.getProfileImageUrl())
                .createdAt(member.getCreatedAt())
                .solvedAcId(member.getSolvedAcId())
                .lastProblemSyncTime(member.getLastProblemSyncTime())
                .tier(solvedAcInfo.getTier())
                .rating(solvedAcInfo.getRating())
                .solvedCount(solvedAcInfo.getSolvedCount())
                .correctRate(solvedAcInfo.getCorrectRate())
                .verified(member.isVerified())
                .build();
    }

    /**
     * 닉네임 중복 확인
     */
    @Transactional(readOnly = true)
    public NicknameVerificationDto checkNicknameAvailability(String nickname) {
        boolean isAvailable = !memberRepository.existsBySolvedAcId(nickname);

        return NicknameVerificationDto.builder()
                .available(isAvailable)
                .message(isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.")
                .build();
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateRequest updateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        member.updateProfile(
                updateDto.getName(),
                updateDto.getProfileImage()
        );

        //memberRepository.save(member);
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

        //memberRepository.save(member);
    }


    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(PasswordResetDto passwordResetDto) {
        Member member = memberRepository.findBySolvedAcId(passwordResetDto.getSolvedAcId())
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다. "));

        // 새 비밀번호 설정
        member.updatePassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    /**
     * 회원 프로필 조회
     */
    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(String solvedAcId) {

        Member member = memberRepository.findBySolvedAcId(solvedAcId)
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버가 없습니다. solvedAcId =" + solvedAcId));

        return MemberProfileResponse.from(member);
    }

    /**
     * 가입한 팀 목록 조회
     */
    @Transactional(readOnly = true)
    public MemberTeamsResponse getMemberTeams(String solvedAcId) {
        Member member = memberRepository.findWithTeamsBySolvedAcId(solvedAcId)
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버가 없습니다. solvedAcId =" + solvedAcId));

        List<TeamSimpleResponse> teams = member.getTeamMembers().stream()
                .filter(tm -> !tm.getTeam().isDeleted())
                .map(TeamSimpleResponse::from)
                .collect(Collectors.toList());

        return MemberTeamsResponse.from(teams);
    }

    /**
     * 프로필 이미지 업로드 (S3)
     */
    @Transactional
    public String saveProfileImage(MultipartFile file, String solvedAcId) {
        try {
            // 1. 사용자 조회
            Member member = memberRepository.findBySolvedAcId(solvedAcId)
                    .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

            // 2. 기존 프로필 이미지가 있다면 S3에서 삭제
            if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
                awsS3Service.deleteFileByUrl(member.getProfileImageUrl());
            }

            // 3. S3에 새 이미지 업로드
            String imageUrl = awsS3Service.uploadSingleFile(file);

            // 4. DB 업데이트
            member.updateProfileImage(imageUrl);

            return imageUrl;

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 프로필 이미지 삭제
     */
    @Transactional
    public void deleteProfileImage(String solvedAcId) {
        try {
            Member member = memberRepository.findBySolvedAcId(solvedAcId)
                    .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

            if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
                // S3에서 이미지 삭제
                awsS3Service.deleteFileByUrl(member.getProfileImageUrl());
                
                // DB에서 이미지 URL 제거
                member.updateProfileImage(null);
            }
        } catch (Exception e) {
            log.error("프로필 이미지 삭제 실패: {}", e.getMessage(), e);
            throw new RuntimeException("프로필 이미지 삭제에 실패했습니다.", e);
        }
    }
}