package force.ssafy.domain.ranking.service.dto;

import force.ssafy.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record RankingDto(
        Long memberId,
        String memberName,
        String solvedAcId,
        String profileImage,
        Integer score,
        Integer rank,
        Integer solvedCount
) {
    public static RankingDto of(Member member, Integer score, Integer rank, Integer solvedCount) {
        return RankingDto.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .solvedAcId(member.getSolvedAcId())
                .profileImage(member.getProfileImage())
                .score(score)
                .rank(rank)
                .solvedCount(solvedCount)
                .build();
    }
}
