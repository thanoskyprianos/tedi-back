package com.network.network.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network.network.security.jwt.TokenRepr;
import com.network.network.user.service.UserService;
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
    private ObjectMapper objectMapper;

    @Resource
    private UserService userService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
    {
        try {
            userService.invalidateTokens(objectMapper.readValue(request.getInputStream(), TokenRepr.class));
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
