package com.network.network.role.exception;

import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final String role;

    public RoleNotFoundException(String role) {
        super("Role " + role + " not found");
        this.role = role;
    }
}
