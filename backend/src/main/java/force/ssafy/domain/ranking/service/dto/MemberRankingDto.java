package force.ssafy.domain.ranking.service.dto;

import force.ssafy.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberRankingDto(
        Long memberId,
        String memberName,
        String solvedAcId,
        String profileImage,
        Integer score,
        Integer rank,
        Integer solvedCount
) {
    public static MemberRankingDto of(Member member, Integer score, Integer rank, Integer solvedCount) {
        return MemberRankingDto.builder()
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
