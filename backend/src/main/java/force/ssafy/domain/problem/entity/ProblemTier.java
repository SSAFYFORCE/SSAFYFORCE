package force.ssafy.domain.problem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum ProblemTier {
    UNRATED("Unrated", 0),

    // Bronze 티어
    B5("Bronze V", 1),
    B4("Bronze IV", 2),
    B3("Bronze III", 3),
    B2("Bronze II", 4),
    B1("Bronze I", 5),

    // Silver 티어
    S5("Silver V", 6),
    S4("Silver IV", 7),
    S3("Silver III", 8),
    S2("Silver II", 9),
    S1("Silver I", 10),

    // Gold 티어
    G5("Gold V", 11),
    G4("Gold IV", 12),
    G3("Gold III", 13),
    G2("Gold II", 14),
    G1("Gold I", 15),

    // Platinum 티어
    P5("Platinum V", 16),
    P4("Platinum IV", 17),
    P3("Platinum III", 18),
    P2("Platinum II", 19),
    P1("Platinum I", 20),

    // Diamond 티어
    D5("Diamond V", 21),
    D4("Diamond IV", 22),
    D3("Diamond III", 23),
    D2("Diamond II", 24),
    D1("Diamond I", 25),

    // Ruby 티어
    R5("Ruby V", 26),
    R4("Ruby IV", 27),
    R3("Ruby III", 28),
    R2("Ruby II", 29),
    R1("Ruby I", 30);

    private final String tier;
    private final int point;
}