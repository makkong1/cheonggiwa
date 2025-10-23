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
public class Security {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS 설정 추가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/hello",
                                "/api/user", // 회원가입 (POST)
                                "/api/user/login", // 로그인
                                "/api/user/logout", // 로그아웃
                                "/api/user/me", // 현재 사용자 정보
                                "/api/room/**", // 객실 관련 API
                                "/api/booking/**", // 예약 관련 API
                                "/api/review/**") // 리뷰 관련 API
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form.disable()) // ✅ React에서 fetch로 로그인하니까 formLogin은 끔
                .httpBasic(basic -> basic.disable()); // ✅ REST API니까 기본 인증도 끔

        return http.build();
    }

    // React(3000)에서 오는 요청 허용 (세션 쿠키 포함)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키(세션) 허용
        config.setAllowedOrigins(List.of("http://localhost:3000")); // React 개발 서버 주소
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
