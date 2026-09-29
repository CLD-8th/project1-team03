package team1.foody.auth;

import team1.foody.auth.dto.LoginRequest;
import team1.foody.auth.dto.ReissueRequest;
import team1.foody.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 요청 수신.
 *
 * 주소에 동사를 두지 않는 원칙의 예외.
 * 관례가 널리 자리 잡아 자원으로 표현하면 오히려 낯설어짐.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody ReissueRequest request) {
        String access = authService.reissue(request.refreshToken());
        return ResponseEntity.ok(new TokenResponse(access, null, null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.userId(), request.password()));
    }

    /**
     * 로그아웃.
     *
     * 갱신 토큰을 제거하고 접근 토큰을 차단.
     * 인증이 필요하므로 신원과 토큰을 함께 확보.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Long memberId,
                                       @RequestHeader("Authorization") String header) {

        authService.logout(memberId, header.substring(7));
        return ResponseEntity.noContent().build();
    }
}
