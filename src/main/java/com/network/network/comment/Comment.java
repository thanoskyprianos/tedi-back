package com.network.network.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.notification.modules.CommentNotification;
import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    @JsonView(View.AsProfessional.class)
    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(View.AsProfessional.class)
    private Date created;

    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "connected"})
    @ManyToOne @JoinColumn(name = "user")
    private User user;

    @JsonView(View.AsAdmin.class)
    @ManyToOne @JoinColumn(name = "post")
    @JsonIgnoreProperties({"likedBy", "comments"})
    private Post post;

    @JsonIgnore
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<CommentNotification> commentNotifications = new ArrayList<>();

    public void addCommentNotification(CommentNotification commentNotification) {
        commentNotifications.add(commentNotification);
    }
}
