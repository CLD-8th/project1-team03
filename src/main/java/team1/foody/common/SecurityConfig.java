package team1.foody.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team1.foody.auth.JwtAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorWriter securityErrorWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // 화면 / 정적 파일
                        // ==============================
                        .requestMatchers(
                                "/",
                                "/shops",
                                "/shops/**",
                                "/login",
                                "/signup",
                                "/mypage",
                                "/*.html",
                                "/*.css",
                                "/*.js",
                                "/*.png",
                                "/*.jpg",
                                "/*.jpeg",
                                "/*.gif",
                                "/css/**",
                                "/js/**",
                                "/uploads/**",
                                "/img/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ==============================
                        // 마이페이지 API
                        // 반드시 로그인 필요
                        // ==============================
                        .requestMatchers(
                                "/api/members/me",
                                "/api/members/me/**"
                        ).authenticated()

                        // ==============================
                        // 회원가입
                        // ==============================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/members"
                        ).permitAll()

                        // ==============================
                        // 로그인 / 토큰 재발급
                        // ==============================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/reissue"
                        ).permitAll()

                        // ==============================
                        // 로그아웃
                        // ==============================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/logout"
                        ).authenticated()

                        // ==============================
                        // Health Check
                        // ==============================
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        .requestMatchers(
                                "/actuator/**"
                        ).authenticated()

                        // ==============================
                        // 게시글 공개 조회
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/posts",
                                "/api/posts/*",
                                "/api/posts/*/comments"
                        ).permitAll()

                        // ==============================
                        // 리뷰 공개 조회
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/shops/*/reviews"
                        ).permitAll()

                        // ==============================
                        // 일반 회원 조회
                        // 단 /me 는 위에서 이미 authenticated
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/members/*"
                        ).permitAll()

                        // ==============================
                        // 나머지 로그인 필요
                        // ==============================
                        .anyRequest().authenticated()
                )

                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(
                                securityErrorWriter.entryPoint()
                        )
                        .accessDeniedHandler(
                                securityErrorWriter.accessDeniedHandler()
                        )
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}