package team1.foody.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        log.info("JWT 요청 URI = {}", request.getRequestURI());
        log.info("JWT 토큰 존재 = {}", token != null);

        if (token != null) {

            boolean valid = tokenProvider.isValid(token);
            log.info("JWT valid = {}", valid);

            if (valid) {

                boolean accessToken = tokenProvider.isAccessToken(token);
                log.info("JWT accessToken = {}", accessToken);

                boolean blocked = tokenStore.isBlocked(token);
                log.info("JWT blocked = {}", blocked);

                if (accessToken && !blocked) {

                    Long memberId =
                            tokenProvider.getMemberId(token);

                    log.info("JWT memberId = {}", memberId);

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    memberId,
                                    null,
                                    Collections.emptyList()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(
            HttpServletRequest request
    ) {

        String header =
                request.getHeader("Authorization");

        if (
                header == null
                        ||
                        !header.startsWith("Bearer ")
        ) {
            return null;
        }

        return header.substring(7);
    }
}