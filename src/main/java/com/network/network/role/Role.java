package com.network.network.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter @Setter
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnoreProperties("role")
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }
}
