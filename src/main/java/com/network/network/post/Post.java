package com.network.network.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
import com.network.network.misc.View;
import com.network.network.notification.modules.InterestNotification;
import com.network.network.notification.modules.LikeNotification;
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

    // either true or false
    @Column(nullable = false)
    @JsonView(View.AsProfessional.class)
    private boolean isPost;

    @Column(nullable = false)
    @JsonView(View.AsProfessional.class)
    private boolean isJobOffer;

    @Column(columnDefinition = "TEXT")
    @JsonView(View.AsProfessional.class)
    private String text;

    @Column(columnDefinition = "TEXT")
    @JsonView(View.AsProfessional.class)
    private String skills;

    @CreationTimestamp
    @Column(updatable=false)
    @JsonView(View.AsProfessional.class)
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
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "connected"})
    private User user;

    @ManyToMany
    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "connected"})
    @JoinTable(name = "post_likes",
        joinColumns = @JoinColumn(name = "post"),
        inverseJoinColumns = @JoinColumn(name = "user"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"post", "user"})
    )
    private Set<User> likedBy = new HashSet<>();

    @JsonView(View.AsAdmin.class)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("post")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<LikeNotification> likeNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<InterestNotification> interestNotifications = new ArrayList<>();

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

    public void addLikedNotification(LikeNotification likedNotification) {
        likeNotifications.add(likedNotification);
    }

    public void addInterestNotification(InterestNotification interestNotification) {
        interestNotifications.add(interestNotification);
    }
}
