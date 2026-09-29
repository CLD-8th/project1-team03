package team1.foody.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 토큰 설정 값 보관.
 *
 * 환경별 파일의 값이 이 형태로 전달됨.
 * 키 이름이 틀리면 기동 시점에 결속이 실패하므로 오타를 일찍 발견 가능.
 */
@ConfigurationProperties(prefix = "board.jwt")
public record JwtProperties(
        String secret,
        long accessExpireMinutes,
        long refreshExpireDays
) {
}
