package force.ssafy.domain.solvedac.service;

import force.ssafy.domain.solvedac.entity.SolvedAcUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedAcApiService {
    private final WebClient webClient;

    @Value("${solved.ac.api.base-url:https://solved.ac/api/v3}")
    private String baseUrl;

    public SolvedAcUserInfo getUserInfo(String solvedAcId) {
        try {
            // solved.ac API 호출
            ResponseEntity<Map> response = webClient
                    .get()
                    .uri(baseUrl + "/user/show?handle={handle}", solvedAcId)
                    .retrieve()
                    .toEntity(Map.class)
                    .block();

            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
                log.error("solved.ac API 호출 실패: {}", solvedAcId);
                throw new RuntimeException("solved.ac API 호출 실패");
            }

            Map<String, Object> userData = response.getBody();
            if (userData == null) {
                throw new RuntimeException("solved.ac API 응답 데이터가 없습니다.");
            }

            // solved.ac API 응답 데이터 파싱
            String tier = parseTier((Integer) userData.get("tier"));
            int rating = ((Number) userData.get("rating")).intValue();
            int solvedCount = ((Number) userData.getOrDefault("solvedCount", 0)).intValue();

            // 정답률 계산 (맞은 문제 수 / 전체 시도 수)
            int totalTries = ((Number) userData.getOrDefault("totalTries", 0)).intValue();
            double correctRate = totalTries > 0 ?
                    (solvedCount * 100.0 / totalTries) : 0.0;

            return SolvedAcUserInfo.builder()
                    .tier(tier)
                    .rating(rating)
                    .solvedCount(solvedCount)
                    .correctRate(Math.round(correctRate * 10) / 10.0)  // 소수점 첫째자리까지
                    .build();
        } catch (Exception e) {
            log.error("solved.ac 사용자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("solved.ac 사용자 정보 조회 실패", e);
        }
    }

    private String parseTier(Integer tierCode) {
        // solved.ac 티어 코드를 문자열로 변환
        String[] tiers = {
                "Unrated",
                "Bronze V", "Bronze IV", "Bronze III", "Bronze II", "Bronze I",
                "Silver V", "Silver IV", "Silver III", "Silver II", "Silver I",
                "Gold V", "Gold IV", "Gold III", "Gold II", "Gold I",
                "Platinum V", "Platinum IV", "Platinum III", "Platinum II", "Platinum I",
                "Diamond V", "Diamond IV", "Diamond III", "Diamond II", "Diamond I",
                "Ruby V", "Ruby IV", "Ruby III", "Ruby II", "Ruby I"
        };

        return tierCode != null && tierCode >= 0 && tierCode < tiers.length ?
                tiers[tierCode] : "Unrated";
    }
}