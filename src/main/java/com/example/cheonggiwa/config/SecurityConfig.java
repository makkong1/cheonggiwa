package com.example.cheonggiwa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF 비활성화 (React와 API 통신용)
                                .csrf(csrf -> csrf.disable())

                                // CORS 설정
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // 요청별 접근 제어
                                .authorizeHttpRequests(auth -> auth
                                                // 비로그인 상태에서도 접근 가능한 URL
                                                .requestMatchers(
                                                                "/api/user/register", // 회원가입
                                                                "/api/user/login", // 로그인
                                                                "/api/user/logout", // 로그아웃
                                                                "/api/room/**", // 객실 목록, 상세보기
                                                                "/api/hello")
                                                .permitAll()

                                                // 로그인된 사용자만 접근 가능
                                                .requestMatchers(
                                                                "/api/user/me", // 내 정보
                                                                "/api/booking/**", // 예약
                                                                "/api/review/**" // 리뷰
                                                ).authenticated()

                                                // 그 외는 기본적으로 인증 필요
                                                .anyRequest().authenticated())

                                // 폼 로그인 & 기본 인증 비활성화 (React fetch로 처리)
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())

                                // 세션 사용 허용
                                .sessionManagement(session -> session
                                                .maximumSessions(1) // 동시 로그인 1명 제한 (선택)
                                );

                return http.build();
        }

        // ✅ 6. CORS 설정 — React(3000)에서 오는 요청만 허용
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true); // 세션 쿠키 허용
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }
}
