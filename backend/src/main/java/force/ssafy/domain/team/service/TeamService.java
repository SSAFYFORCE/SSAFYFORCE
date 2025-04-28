package force.ssafy.domain.team.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.team.dto.response.TeamResponse;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 해당하는 teamId 에 대한 팀명과 소속 팀원들을 가져오는 메서드
     * @param teamId
     * @return TeamResponse
     */
    public TeamResponse findTeamDetail(Long teamId) {
        log.info("findTeamDetail 실행");
        Team team = teamRepository.findById(teamId).get();

        List<TeamMemberDto> members = teamMemberRepository.findTeamMemberDtoByTeamId(teamId);

        // Member → MemberResponse 변환
        List<TeamMemberResponse> memberResponses = members.stream()
                .map(TeamMemberResponse::from)
                .toList();

        return TeamResponse.from(team.getName(), memberResponses);
    }

}
