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
import java.util.Optional;

@Component
public class JwtUtil {
    @Resource
    JwtTokenRepository tokenRepository;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expirationMs}")
    private long expirationMs;

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

    public String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.expirationMs))
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

    // could change
    public boolean validateToken(String token) {
        Optional<JwtToken> jwtToken = tokenRepository.findById(token);
        JwtToken jwtTokenObj = null;

        try {
            if (jwtToken.isEmpty()) {
                throw new Exception("Invalid token");
            }

            jwtTokenObj = jwtToken.get();

            if (jwtToken.get().isInvalid()) {
                throw new Exception("Token is invalid");
            }

            Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(token);
        }
        catch (ExpiredJwtException e) {
            jwtTokenObj.setInvalid(true);
            tokenRepository.save(jwtTokenObj);
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }
}
