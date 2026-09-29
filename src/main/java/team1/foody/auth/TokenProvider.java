package team1.foody.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/**
 * 토큰 발급과 검증.
 *
 * 내용은 감춰지지 않으므로 감춰야 할 값을 담지 않음.
 * 서명으로 위조만 차단.
 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;

    /**
     * 서명 열쇠 생성.
     *
     * 설정 값에서 매번 만들며 32바이트 미만이면 예외가 발생.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueAccessToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("type", TYPE_ACCESS)
                .issuedAt(now)
                .expiration(new Date(
                        now.getTime()
                                + jwtProperties.accessExpireMinutes() * 60 * 1000L
                ))
                .signWith(key())
                .compact();
    }

    /**
     * 갱신 토큰 발급.
     *
     * 권한을 담지 않음.
     * 발급 시점의 권한이 오래 유지되면 변경이 즉시 반영되지 않으므로
     * 재발급 시점에 조회.
     */
    public String issueRefreshToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("type", TYPE_REFRESH)
                .issuedAt(now)
                .expiration(new Date(now.getTime()
                        + jwtProperties.refreshExpireDays() * 24 * 60 * 60 * 1000L))
                .signWith(key())
                .compact();
    }

    /**
     * 용도 확인.
     *
     * 구분하지 않으면 갱신 토큰으로도 일반 요청이 통과하여
     * 접근 토큰의 만료를 짧게 둔 의미가 소멸.
     */
    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parse(token).get("type", String.class));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parse(token).get("type", String.class));
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 회원 식별자 확인.
     *
     * 요청에 담긴 값이 아니라 서명이 확인된 토큰에서 꺼내므로 위조가 불가.
     */
    public Long getMemberId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }



    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 남은 만료 시간.
     *
     * 차단 목록에 보관할 기간을 정하는 데 사용.
     * 이미 만료된 값은 영으로 반환.
     */
    public Duration remainingTime(String token) {
        try {
            Date expiration = parse(token).getExpiration();

            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? Duration.ofMillis(remaining) : Duration.ZERO;

        } catch (Exception e) {
            return Duration.ZERO;
        }
    }
}
