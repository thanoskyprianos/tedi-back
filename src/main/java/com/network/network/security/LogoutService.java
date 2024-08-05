package com.network.network.security;

import com.network.network.security.jwt.JwtToken;
import com.network.network.security.jwt.JwtTokenRepository;
import com.network.network.security.jwt.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {
    @Resource
    private JwtUtil jwtUtil;

    @Resource
    JwtTokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
    {
        String token = jwtUtil.extractToken(request);
        if (token == null) {
            return;
        }

        JwtToken jwtToken = tokenRepository.findById(token).orElse(null);
        if (jwtToken == null) { return; }

        jwtToken.setInvalid(true);
        tokenRepository.save(jwtToken);
        SecurityContextHolder.clearContext();
    }
}
