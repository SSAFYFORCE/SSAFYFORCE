package force.ssafy.domain.team.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.team.dto.request.TeamCreateRequest;
import force.ssafy.domain.team.dto.response.TeamListResponse;
import force.ssafy.domain.team.dto.response.TeamResponse;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import force.ssafy.domain.teamMember.entity.TeamMember;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import force.ssafy.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;

    /**
     * 해당하는 teamId 에 대한 팀 정보와 소속 팀원들을 가져오는 메서드
     * @param teamId
     * @return TeamResponse
     */
    public TeamResponse findTeamDetail(Long teamId) {
        log.info("findTeamDetail 실행");
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("해당 팀이 없습니다. id=" + teamId));

        List<TeamMemberDto> members = teamMemberRepository.findTeamMemberDtoByTeamId(teamId);

        // Member → MemberResponse 변환
        List<TeamMemberResponse> memberResponses = members.stream()
                .map(TeamMemberResponse::from)
                .toList();

        return TeamResponse.of(team, memberResponses);
    }

    /**
     * 모든 팀 정보와 소속 팀원들을 가져오는 메서드
     * @return TeamListResponse
     */
    public TeamListResponse findAllTeams() {
        log.info("findAllTeams 실행");

        List<Team> all = teamRepository.findAll();
        List<TeamResponse> teamList = new ArrayList<>();

        for (Team team : all) {
            List<TeamMemberDto> members = teamMemberRepository.findPreviewMemberByTeamId(team.getId());

            // Member → MemberResponse 변환
            List<TeamMemberResponse> memberResponses = members.stream()
                    .map(TeamMemberResponse::from)
                    .toList();

            teamList.add(TeamResponse.of(team, memberResponses));
        }

        return TeamListResponse.to(teamList);
    }

    /**
     * 팀 생성
     * @param teamCreateRequest
     */
    @Transactional
    public void save(TeamCreateRequest teamCreateRequest) {
        log.info("team save 호출");

        Team team = teamCreateRequest.toEntity();
        teamRepository.save(team);
    }

    /**
     * 팀 가입
     *
     * @param memberId 로그인한 사용자 ID
     * @param teamId   가입하려는 팀 ID
     */
    @Transactional
    public void joinTeam(Long memberId, Long teamId) {
        // 1) 팀 존재 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("해당 팀이 없습니다. id=" + teamId));

        // 2) 회원 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 없습니다. id=" + memberId));

        // 3) 중복 가입 방지
        boolean already = teamMemberRepository.existsByTeamIdAndMemberId(teamId, memberId);
        if (already) {
            throw new IllegalStateException("이미 가입된 팀입니다.");
        }

        // 4) 가입 가능한 인원수 체크 (선택)
        // if (team.getTeamMembers().size() >= team.getMaxMembers()) {
        //     throw new IllegalStateException("팀 정원이 가득 찼습니다.");
        // }

        // 5) TeamMember 엔티티 생성 및 저장
        TeamMember tm = TeamMember.builder()
                .team(team)
                .member(member)
                .joinedAt(LocalDateTime.now().toString())
                .role(MemberRole.MEMBER)
                .build();

        teamMemberRepository.save(tm);
    }
}
