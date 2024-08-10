package com.network.network.user.repr;

import com.network.network.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthResponse extends UserRepr {
    String token;

    public AuthResponse(User user, String token) {
        super(user);
        this.token = token;
    }
}
