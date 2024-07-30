package com.network.network.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final int id;

    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
        this.id = id;
    }
}
