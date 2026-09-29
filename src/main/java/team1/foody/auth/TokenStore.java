package team1.foody.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 토큰 보관과 차단.
 *
 * 갱신 토큰은 회원마다 하나만 보관하므로 새로 발급하면 이전 값이 무효.
 * 접근 토큰은 만료가 남은 동안만 차단 목록에 보관.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenStore {

    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String BLOCK_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void saveRefresh(Long memberId, String token, Duration ttl) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + memberId, token, ttl);
    }

    /**
     * 보관된 값과 대조.
     *
     * 로그아웃으로 제거되었다면 서명이 유효해도 거부.
     */
    public boolean matchesRefresh(Long memberId, String token) {
        String stored = redisTemplate.opsForValue().get(REFRESH_PREFIX + memberId);
        return token.equals(stored);
    }

    public void removeRefresh(Long memberId) {
        redisTemplate.delete(REFRESH_PREFIX + memberId);
    }

    /**
     * 접근 토큰 차단.
     *
     * 남은 만료 시간만큼만 보관하므로 자동으로 정리됨.
     * 이미 만료된 값은 등록이 불필요.
     */
    public void block(String token, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        redisTemplate.opsForValue().set(BLOCK_PREFIX + token, "blocked", ttl);
    }

    /**
     * 차단 여부.
     *
     * 저장소에 접근하지 못하면 통과로 판단.
     * 서비스가 멈추지 않도록 하는 선택이며 기록을 남겨 장애를 인지.
     */
    public boolean isBlocked(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLOCK_PREFIX + token));
        } catch (Exception e) {
            log.warn("차단 목록 확인 실패", e);
            return false;
        }
    }
}
