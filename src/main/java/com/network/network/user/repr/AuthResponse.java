package com.network.network.user.repr;

import com.network.network.security.jwt.TokenRepr;
import com.network.network.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthResponse extends User {
    private TokenRepr tokens;

    public AuthResponse(User user, String accessToken, String refreshToken) {
        super(user);
        this.tokens = new TokenRepr(accessToken, refreshToken);
    }
}
