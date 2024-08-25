package com.network.network.security.jwt;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Resource
    JwtUtil jwtUtil;

    @Resource
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtUtil.extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        JwtToken jwtToken = jwtUtil.validateToken(token);
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // only use refresh token for refreshing
        if (request.getRequestURI().equals("/users/refresh")) {
            if (jwtToken.getType() != TokenType.REFRESH) {
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            if (jwtToken.getType() != TokenType.ACCESS) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        setContext(request, token);

        filterChain.doFilter(request, response);
    }

    public void setContext(HttpServletRequest request, String token) {
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(jwtUtil.extractUsername(token));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken (
                userDetails, null, userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
