package team1.foody.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShopRankingService {

    private static final String RANKING_KEY = "shop:ranking";
    private final StringRedisTemplate redisTemplate;

    // 상세 조회 시 점수 1 증가
    public void increaseScore(Long shopId) {
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(shopId), 1);
    }

    // 점수 상위 N개 상점 ID 조회
    public List<Long> topShopIds(int size) {
        Set<String> ids = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, size - 1);
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .map(Long::valueOf)
                .toList();
    }
}
