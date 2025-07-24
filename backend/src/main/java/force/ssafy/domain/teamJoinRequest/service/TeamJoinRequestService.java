package force.ssafy.domain.teamJoinRequest.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.entity.MemberRole;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamJoinRequest.dto.MyTeamJoinRequestListDto;
import force.ssafy.domain.teamJoinRequest.dto.TeamJoinRequestDto;
import force.ssafy.domain.teamJoinRequest.entity.JoinStatus;
import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import force.ssafy.domain.teamJoinRequest.repository.TeamJoinRequestRepository;
import force.ssafy.domain.teamMember.entity.TeamMember;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamJoinRequestService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    @Transactional
    public void requestJoin(Long teamId, Long memberId) {
        log.info("requestJoin 호출");

        // 1) 팀 존재 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new force.ssafy.global.error.exception.EntityNotFoundException("해당 팀이 없습니다. id=" + teamId));

        // 2) 회원 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new force.ssafy.global.error.exception.EntityNotFoundException("해당 사용자가 없습니다. id=" + memberId));

        // 이미 가입된 팀인지 확인
        checkTeamMember(teamId, memberId);

        TeamJoinRequest request = TeamJoinRequest.joinRequest(team, member);
        teamJoinRequestRepository.save(request);
    }

    private void checkTeamMember(Long teamId, Long memberId) {
        if (teamJoinRequestRepository.findByTeam_IdAndRequester_IdAndStatus
                (teamId, memberId, JoinStatus.PENDING).isPresent()) {
            throw new IllegalStateException("이미 가입 요청이 대기 중입니다.");
        }

        if (teamMemberRepository.existsByTeamIdAndMemberId(teamId, memberId)) {
            throw new IllegalStateException("이미 가입된 팀입니다.");
        }
    }

    @Transactional
    public void approve(Long teamId, Long reqId, Long memberId) {
        log.info("approve 호출");

        checkTeamLeader(teamId, memberId);

        TeamJoinRequest request = teamJoinRequestRepository.findByIdAndTeam_IdAndStatus(reqId, teamId, JoinStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("해당 요청이 없습니다. "));

        request.approve();

        TeamMember teamMember = TeamMember.create(request.getRequester(), request.getTeam(), MemberRole.MEMBER);
        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void reject(Long teamId, Long reqId, Long memberId) {
        log.info("reject 호출");

        checkTeamLeader(teamId, memberId);

        TeamJoinRequest request = teamJoinRequestRepository.findByIdAndTeam_IdAndStatus(reqId, teamId, JoinStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("해당 요청이 없습니다. "));

        // 삭제 처리 말고 걍 삭제
        teamJoinRequestRepository.delete(request);
        //request.reject();
    }

    @Transactional
    public void cancel(Long teamId, Long memberId) {
        log.info("cancel 호출");

        TeamJoinRequest req = teamJoinRequestRepository
                .findByTeam_IdAndRequester_IdAndStatus(teamId, memberId, JoinStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("대기 중인 요청이 없습니다."));

        req.cancel();
    }

    public List<TeamJoinRequestDto> getRequestList(Long teamId, Long memberId) {
        log.info("getRequestList : 팀 가입 요청 다건 호출");

        checkTeamLeader(teamId, memberId);

        List<TeamJoinRequest> requestList = teamJoinRequestRepository.findByTeam_IdAndStatus(teamId, JoinStatus.PENDING);

        return requestList.stream()
                .map(TeamJoinRequestDto::from)
                .toList();
    }

    public TeamJoinRequestDto getRequest(Long teamId, Long reqId, Long memberId) {
        log.info("getRequests : 팀 가입 요청 단건 호출");

        TeamJoinRequest request = teamJoinRequestRepository.findByTeam_IdAndRequester_IdAndStatus(teamId, reqId, JoinStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("해당 요청이 없습니다. "));

        return TeamJoinRequestDto.from(request);
    }

    private void checkTeamLeader(Long teamId, Long memberId) {
        TeamMember tm = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId);
        if (tm == null || tm.getRole() != MemberRole.LEADER) {
            throw new IllegalStateException("팀 리더만 가능한 접근입니다.");
        }
    }

    public MyTeamJoinRequestListDto getMyRequestList(Long memberId) {
        List<TeamJoinRequest> list = teamJoinRequestRepository.findAllByRequester_IdAndStatus(memberId, JoinStatus.PENDING);

        return MyTeamJoinRequestListDto.from(list);
    }
}
