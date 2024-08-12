package com.network.network.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;

    @Column(updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created;

    @JsonIgnore
    @JoinTable(name = "post_media",
        joinColumns = @JoinColumn(name = "post"),
        inverseJoinColumns = @JoinColumn(name = "media")
    )
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Media> mediaList = new ArrayList<>();

    @ManyToOne @JsonIgnore
    private User user;

    @JsonIgnore @ManyToMany
    @JoinTable(name = "post_likes",
        joinColumns = @JoinColumn(name = "post"),
        inverseJoinColumns = @JoinColumn(name = "user"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"post", "user"})
    )
    private Set<User> likedBy = new HashSet<>();

    @JsonIgnore
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
