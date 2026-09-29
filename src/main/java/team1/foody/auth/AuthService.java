package team1.foody.auth;

import team1.foody.auth.dto.TokenResponse;
import team1.foody.member.Member;
import team1.foody.member.MemberRepository;
import team1.foody.common.NotFoundException;
import team1.foody.common.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 로그인 처리.
 *
 * 실패 사유를 구분해 응답하지 않음.
 * 구분하면 그 아이디의 가입 여부를 확인하는 수단이 됨.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;
    private final JwtProperties jwtProperties;

    public TokenResponse login(String userId, String password) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("인증 실패"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new UnauthorizedException("인증 실패");
        }

        String access = tokenProvider.issueAccessToken(member.getId());
        String refresh = tokenProvider.issueRefreshToken(member.getId());

        // 갱신 토큰을 보관.
        // 회원마다 하나만 두므로 다시 로그인하면 이전 값이 무효.
        tokenStore.saveRefresh(member.getId(), refresh,
                Duration.ofDays(jwtProperties.refreshExpireDays()));

        return new TokenResponse(access, refresh, member.getId(), member.getNickname());
    }

    /**
     * 재발급.
     *
     * 갱신 토큰을 확인하고 새 접근 토큰을 발급.
     * 권한은 이 시점에 조회하므로 변경이 반영됨.
     */
    public String reissue(String refreshToken) {
        if (!tokenProvider.isValid(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("갱신 토큰이 유효하지 않음");
        }

        Long memberId = tokenProvider.getMemberId(refreshToken);

        // 보관된 값과 대조.
        // 서명이 유효해도 로그아웃으로 제거되었다면 거부.
        if (!tokenStore.matchesRefresh(memberId, refreshToken)) {
            throw new UnauthorizedException("등록되지 않은 갱신 토큰");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원 부재"));

        return tokenProvider.issueAccessToken(member.getId());
    }

    /**
     * 로그아웃.
     *
     * 갱신 토큰을 제거하고 접근 토큰을 차단 목록에 등록.
     * 둘 다 하지 않으면 만료까지 사용이 가능.
     */
    public void logout(Long memberId, String accessToken) {
        tokenStore.removeRefresh(memberId);

        // 남은 만료 시간만큼만 보관.
        Duration remaining = tokenProvider.remainingTime(accessToken);
        tokenStore.block(accessToken, remaining);
    }
}
