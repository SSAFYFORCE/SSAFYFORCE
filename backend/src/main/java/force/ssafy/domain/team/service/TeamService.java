package force.ssafy.domain.team.service;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.team.dto.request.TeamCreateRequest;
import force.ssafy.domain.team.dto.response.TeamListResponse;
import force.ssafy.domain.team.dto.response.TeamResponse;
import force.ssafy.domain.team.entity.Team;
import force.ssafy.domain.team.repository.TeamRepository;
import force.ssafy.domain.teamMember.dto.TeamMemberDto;
import force.ssafy.domain.teamMember.dto.response.TeamMemberResponse;
import force.ssafy.domain.teamMember.repository.TeamMemberRepository;
import force.ssafy.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
