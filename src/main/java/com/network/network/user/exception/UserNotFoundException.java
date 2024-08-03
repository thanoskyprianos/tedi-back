package com.network.network.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
    }

    public UserNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}
