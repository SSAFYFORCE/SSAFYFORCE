package force.ssafy.global.security.jwt;

import force.ssafy.domain.auth.dto.response.TokenDto;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenValidTime;

    // 로그아웃된 토큰 저장소 (실제로는 Redis 사용 권장)
    private final Map<String, Boolean> blacklistedTokens = new ConcurrentHashMap<>();

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성
    public TokenDto createToken(Long memberId, String nickname) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
        claims.put("nickname", nickname);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenValidTime / 1000)
                .build();
    }

    // 토큰에서 회원 ID 추출
    public Long getMemberIdFromToken(String token) {
        String subject = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.parseLong(subject);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 블랙리스트 체크
            if (blacklistedTokens.containsKey(token)) {
                return false;
            }

            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    // 토큰 무효화 (로그아웃)
    public void invalidateToken(String token) {
        blacklistedTokens.put(token, true);
    }
}