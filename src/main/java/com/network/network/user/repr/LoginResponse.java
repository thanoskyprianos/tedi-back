package com.network.network.user.repr;

import com.network.network.security.repr.JwtRepr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;

@Getter @Setter @AllArgsConstructor
public class LoginResponse {
    EntityModel<UserRepr> user;
    JwtRepr jwt;
}
