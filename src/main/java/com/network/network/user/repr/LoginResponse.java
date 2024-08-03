package com.network.network.user.repr;

import com.network.network.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LoginResponse {
    User user;
    String jwt;
}
