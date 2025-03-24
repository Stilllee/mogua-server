package com.fesi6.team1.study_group.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieUtil jwtCookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = getJwtFromRequest(request);
        String refreshToken = getRefreshTokenFromRequest(request);

        if (accessToken != null) {
            try {
                if (jwtTokenProvider.validateAccessToken(accessToken)) {
                    Long userId = jwtTokenProvider.getUserIdFromAccessToken(accessToken);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } else {
                    throw new ExpiredJwtException(null, null, "Access token has expired");
                }
            } catch (ExpiredJwtException e) {
                if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
                    String newAccessToken = jwtTokenProvider.createAccessToken(userId);

                    // 🔹 쿠키 설정 시 response.addHeader() 대신 ResponseCookie 사용
                    ResponseCookie newAccessTokenCookie = jwtCookieUtil.createAccessTokenCookie(newAccessToken);
                    response.setHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\": \"error\", \"message\": \"Access token has expired. Please login again.\"}");
                    return;
                }
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid token.\"}");
                return;
            }
        }

        if (accessToken == null && refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtTokenProvider.createAccessToken(userId);

            ResponseCookie newAccessTokenCookie = jwtCookieUtil.createAccessTokenCookie(newAccessToken);
            response.setHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        response.setHeader("Partitioned", "true");
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}