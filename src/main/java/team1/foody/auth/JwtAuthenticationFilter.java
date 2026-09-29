package team1.foody.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 요청마다 신원 확인.
 *
 * 토큰이 없어도 통과시킴.
 * 공개 경로도 이 여과기를 지나므로 여기서 막으면 공개 경로까지 차단됨.
 * 통과 여부는 접근 규칙이 판단.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        // 접근 토큰만 통과.
        // 갱신 토큰으로 일반 요청이 통과하면 만료를 짧게 둔 의미가 소멸.
        // 접근 토큰만 통과.
        // 갱신 토큰으로 일반 요청이 통과하면 만료를 짧게 둔 의미가 소멸.
        //
        // 차단 목록도 확인.
        // 로그아웃한 토큰은 서명이 유효해도 거부.
        if (token != null && tokenProvider.isValid(token) && tokenProvider.isAccessToken(token)
                && !tokenStore.isBlocked(token)) {
            Long memberId = tokenProvider.getMemberId(token);
            String role = tokenProvider.getRole(token);

            var authentication = new UsernamePasswordAuthenticationToken(
                    memberId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
