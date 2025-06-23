package force.ssafy.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberDto {
    private Long id;
    private String name;
    private String profileImage;
    private LocalDateTime createdAt;
    private String solvedAcId;
    private LocalDateTime lastProblemSyncTime;
    private boolean verified;
    private String tier;        // solved.ac 티어 정보
    private int rating;         // solved.ac 레이팅
    private int solvedCount;    // 해결한 문제 수
    private double correctRate; // 정답률
}
