package com.network.network.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "user")
    private User user;

    @JsonIgnore
    @ManyToOne @JoinColumn(name = "post")
    private Post post;

    private String text;
}
