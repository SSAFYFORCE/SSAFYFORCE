package force.ssafy.domain.teamJoinRequest.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamJoinRequest.dto.TeamJoinRequestDto;
import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import force.ssafy.domain.teamJoinRequest.repository.TeamJoinRequestRepository;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamJoinRequestService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    public void requestJoin(Long teamId, Long memberId) {
        log.info("requestJoin 호출");

        // 1) 팀 존재 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new force.ssafy.global.error.exception.EntityNotFoundException("해당 팀이 없습니다. id=" + teamId));

        // 2) 회원 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new force.ssafy.global.error.exception.EntityNotFoundException("해당 사용자가 없습니다. id=" + memberId));

        // 3) 중복 가입 방지
        boolean already = teamMemberRepository.existsByTeamIdAndMemberId(teamId, memberId);
        if (already) {
            throw new IllegalStateException("이미 가입된 팀입니다.");
        }

        TeamJoinRequest request = TeamJoinRequest.joinRequest(team, member);
        teamJoinRequestRepository.save(request);
    }

    public void approve(Long teamId, Long reqId, Long memberId) {

    }

    public void reject(Long teamId, Long reqId, Long memberId) {

    }

    public List<TeamJoinRequestDto> getRequests(Long teamId, Long memberId) {
        return null;
    }

    public TeamJoinRequestDto getRequest(Long teamId, Long reqId, Long memberId) {
        return null;
    }
}
