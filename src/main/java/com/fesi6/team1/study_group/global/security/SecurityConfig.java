package com.fesi6.team1.study_group.global.security;

import com.fesi6.team1.study_group.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] AUTH_WHITELIST = {
            "/user/kakao/**",// 카카오 로그인 콜백 URL
            "/user/sign-up",
            "/user/sign-in",
            "/user/*/meetups/participating/**",
            "/user/*/reviews/**",
            "/user/*/meetups/created/**",
            "/user/profile/*",
            "/user/reviews/received",
            "/meetups/list",
            "/meetups/*",
            "/wishlist/*",
            "/reviews/list/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers(AUTH_WHITELIST).permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://localhost:3000",
                "https://mogua-g109cgdv1-joshuayeyos-projects.vercel.app",
                "https://mogua.vercel.app"
        ));

        // 모든 HTTP 메서드 허용
        configuration.addAllowedMethod("*");

        // 모든 헤더 허용
        configuration.addAllowedHeader("*");

        // 인증 정보 포함 (credentials: 'include' 허용)
        configuration.setAllowCredentials(true);

        // 클라이언트에서 접근할 수 있도록 노출할 헤더 설정
        configuration.addExposedHeader("Authorization");

        // CORS 설정을 적용할 경로 매핑
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}