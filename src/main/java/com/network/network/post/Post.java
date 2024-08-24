package com.network.network.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Post {
    @JsonView(View.AsProfessional.class)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob @JsonView(View.AsProfessional.class)
    private String text;

    @CreationTimestamp
    @Column(updatable=false)
    @JsonView(View.AsAdmin.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @JsonIgnore
    @JoinTable(name = "post_media",
        joinColumns = @JoinColumn(name = "post"),
        inverseJoinColumns = @JoinColumn(name = "media")
    )
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Media> mediaList = new ArrayList<>();

    @ManyToOne
    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "role", "connected"})
    private User user;

    @ManyToMany
    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "role", "connected"})
    @JoinTable(name = "post_likes",
        joinColumns = @JoinColumn(name = "post"),
        inverseJoinColumns = @JoinColumn(name = "user"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"post", "user"})
    )
    private Set<User> likedBy = new HashSet<>();

    @JsonView(View.AsAdmin.class)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    public Post(String text) { this.text = text; }

    public void addMedia(Media media) {
        mediaList.add(media);
    }

    public void addLikedBy(User user) {
        likedBy.add(user);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
