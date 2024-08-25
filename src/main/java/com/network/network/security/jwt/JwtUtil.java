package com.network.network.security.jwt;

import com.network.network.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Resource
    JwtTokenRepository tokenRepository;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.accessTokenExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${jwt.token.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    public void invalidateAllTokens(User user) {
        user.getJwtTokens().forEach(jwtToken -> {
            jwtToken.setInvalid(true);
            tokenRepository.save(jwtToken);
        });
    }

    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }

    public String generateAccessToken(String subject) {
        return this.generateToken(subject, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String subject) {
        return this.generateToken(subject, refreshTokenExpirationMs);
    }

    private String generateToken(String subject, long expirationMs) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expirationMs))
                .signWith(secretKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public long extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

    public JwtToken validateToken(String token) {
        JwtToken jwtToken;
        try {
            jwtToken = tokenRepository
                    .findById(token)
                    .orElseThrow(() -> new Exception("Token not found"));
        }
        catch (Exception e) {
            return null;
        }

        try {
            if (jwtToken.isInvalid()) {
                throw new Exception("Invalid token");
            }

            Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(token);
        }
        catch (ExpiredJwtException e) {
            jwtToken.setInvalid(true);
            tokenRepository.save(jwtToken);

            System.out.println("Expired token");
            return null;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return jwtToken;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }
}
