package com.network.network.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "user")
    private User user;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "post")
    private Post post;

    private String text;
}
