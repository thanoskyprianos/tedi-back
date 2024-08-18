package com.network.network.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Role {
    @Id
    @JsonView(View.AsAdmin.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonView(View.AsProfessional.class)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }
}
