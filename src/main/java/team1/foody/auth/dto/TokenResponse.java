package team1.foody.auth.dto;

/**
 * 로그인 응답 형태.
 *
 * 두 토큰을 함께 전달.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long memberId,
        String nickname
) {
}
