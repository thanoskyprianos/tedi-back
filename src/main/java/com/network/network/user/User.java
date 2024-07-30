package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.network.network.media.Media;
import com.network.network.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    @Column(unique=true)
    private String email;

    private String password;

    private String phoneNumber;

    @OneToOne(cascade=CascadeType.REMOVE)
    private Media avatar;

    @ManyToOne @JsonIgnoreProperties("users")
    private Role role;
}
