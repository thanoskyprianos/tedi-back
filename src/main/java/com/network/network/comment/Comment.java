package com.network.network.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter @Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonView(View.AsProfessional.class)
    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(View.AsProfessional.class)
    private Date created;

    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "role", "connected"})
    @ManyToOne @JoinColumn(name = "user")
    private User user;

    @JsonIgnore
    @JsonView(View.AsAdmin.class)
    @ManyToOne @JoinColumn(name = "post")
    private Post post;
}
