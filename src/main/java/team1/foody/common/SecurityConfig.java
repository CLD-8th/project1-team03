package team1.foody.common;

import team1.foody.auth.JwtAuthenticationFilter;
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

/**
 * 접근 규칙.
 *
 * 순서대로 대조하므로 개별 규칙을 먼저 두고 전체 대상을 마지막에 배치.
 *
 * 걸러내는 층에서 끝난 요청은 전역 처리기에 도달하지 않으므로
 * 실패 응답을 여기서 따로 지정함.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorWriter securityErrorWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 토큰 방식이므로 상태를 보관하지 않음.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 화면 파일은 인증 없이 제공.
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/img/**").permitAll()
                        // 가입과 로그인은 공개.
                        .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                        // 로그아웃은 누구인지 알아야 하므로 인증 대상.
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 생존 확인은 공개. 배포 도구가 토큰 없이 호출함.
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        // 나머지 운영 경로는 관리자만. 열어 두면 설정과 환경 값이 드러남.
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // 경로만으로 판단 가능한 경우는 설정에서 처리.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 조회는 공개.
                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/members/*").permitAll()
                        // 나머지는 인증 필요.
                        .anyRequest().authenticated())
                // 걸러내는 층의 실패도 같은 형태로 응답.
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(securityErrorWriter.entryPoint())
                        .accessDeniedHandler(securityErrorWriter.accessDeniedHandler()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 비밀번호 변환 도구.
     *
     * 되돌릴 수 없는 방식이며 대조만 가능.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
