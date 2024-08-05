package com.network.network.user.repr;

import com.network.network.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRepr {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;

    public UserRepr(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole().getName();
    }
}
