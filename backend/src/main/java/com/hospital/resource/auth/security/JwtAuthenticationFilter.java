package com.hospital.resource.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && tokenProvider.isTokenValid(token)) {
            Claims claims = tokenProvider.validateToken(token);
            UUID userId = UUID.fromString(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                if ("ADMINISTRATOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                if ("ADMIN".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
                if ("NURSING_OFFICER".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_NURSE"));
                if ("NURSE".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_NURSING_OFFICER"));
                if ("MEDICAL_DOCTOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
                if ("DOCTOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_MEDICAL_DOCTOR"));
                if ("HYGIENE_STAFF".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_CLEANER"));
                if ("CLEANER".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_HYGIENE_STAFF"));
            }
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, username, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
