package com.network.network.security.jwt;

import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter @Setter
public class JwtToken {
    @Id private String token;

    private boolean invalid;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date created;

    @ManyToOne
    private User user;
}
