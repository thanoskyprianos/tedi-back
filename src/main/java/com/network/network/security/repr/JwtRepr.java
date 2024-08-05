package com.network.network.security.repr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class JwtRepr {
    String token;
    long exp;
}
