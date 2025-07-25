package force.ssafy.domain.teamJoinRequest.dto;

import force.ssafy.domain.teamJoinRequest.entity.TeamJoinRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
@Builder
public class MyTeamJoinRequestListDto {

    private List<Long> teamList;

    public static MyTeamJoinRequestListDto from(List<TeamJoinRequest> list) {
        // 1) DTO에 담을 데이터 추출 ― 여기선 teamId만 모은다고 가정
        List<Long> ids = list.stream()
                .map(req -> req.getTeam().getId())
                .toList();   // Java 16+ /  Stream API

        // 2) DTO 인스턴스 생성
        return MyTeamJoinRequestListDto.builder()
                .teamList(ids)
                .build();
    }
}
