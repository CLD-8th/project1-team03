package team1.foody.auth.dto;

/**
 * 로그인 요청 형태.
 */
public record LoginRequest(
        String userId,
        String password
) {
}
