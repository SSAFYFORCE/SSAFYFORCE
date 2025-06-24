package force.ssafy.domain.team.dto.request;

import force.ssafy.domain.team.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TeamCreateRequest {

    private String name;
    private String description;

    public Team toEntity() {
        LocalDateTime now = LocalDateTime.now();

        return Team.builder()
                .name(name)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
