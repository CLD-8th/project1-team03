package team1.foody.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 전역 예외 처리기.
 *
 * 표현 계층이 예외를 잡지 않아도 여기서 받아 같은 형태로 변환함.
 * 위에서부터 대조하며 첫 일치에서 멈추므로 좁은 예외를 먼저 배치함.
 *
 * 걸러내는 층에서 발생한 실패는 여기에 도달하지 않음.
 * 그 경우는 진입점과 거부 처리기가 담당함.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        log.debug("대상 부재: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        // 사유를 그대로 알리면 가입 여부가 드러나므로 문구를 통일함.
        log.debug("인증 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(401, "UNAUTHORIZED", "인증 실패"));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(SecurityException e) {
        log.debug("권한 부족: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(403, "FORBIDDEN", "권한 부재"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegal(IllegalArgumentException e) {
        log.debug("입력값 오류: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "INVALID_INPUT", e.getMessage()));
    }

    /**
     * 검증 표기 위반.
     *
     * 어느 항목이 왜 틀렸는지를 함께 담아 화면이 입력란마다 표시할 수 있게 함.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        e.getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        log.debug("검증 실패: {}", fields);
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "INVALID_INPUT", "입력값 확인 필요", fields));
    }

    /**
     * 위에서 걸리지 않은 나머지.
     *
     * 예상하지 못한 상황이므로 error 수준으로 남기며 예외를 함께 넘겨
     * 발생 위치가 기록되도록 함. 사용자에게는 내부 사정을 알리지 않음.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("처리하지 못한 예외", e);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(500, "INTERNAL_ERROR", "잠시 후 다시 시도"));
    }
}
