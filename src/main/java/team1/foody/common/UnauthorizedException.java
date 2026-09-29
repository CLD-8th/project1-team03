package team1.foody.common;

/**
 * 신원을 확인하지 못했을 때 던지는 예외.
 *
 * 로그인 실패와 갱신 토큰 대조 실패가 여기에 해당함.
 * 입력값 오류와 구분해야 응답 코드가 401 로 유지됨.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
