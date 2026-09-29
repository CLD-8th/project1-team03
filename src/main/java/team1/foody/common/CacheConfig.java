package team1.foody.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 캐시 설정.
 *
 * 이 표기가 없으면 캐시 표기가 전부 무시됨.
 * 오류가 발생하지 않고 매번 저장소를 조회하므로 확인하지 않으면 발견이 곤란.
 */
@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${board.cache.ttl-seconds}")
    private long ttlSeconds;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttlSeconds))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // 현재 판에 맞는 방식을 자동으로 선택.
                // 값에 형 정보가 함께 저장되어 꺼낼 때 되돌릴 수 있음.
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()));
    }
}
