package team1.foody.common;

/**
 * 대상이 없을 때 던지는 예외.
 *
 * 이전에는 대상 부재와 입력값 오류가 모두 IllegalArgumentException 이었음.
 * 표현 계층이 둘을 구분할 수 없어 어느 쪽이든 404 로 응답했음.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
