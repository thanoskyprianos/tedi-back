package com.network.network.user;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {
    private final String email;

    public DuplicateEmailException(String email) {
        super("Duplicate email address: " + email);
        this.email = email;
    }
}
