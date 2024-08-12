package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
import com.network.network.post.Post;
import com.network.network.role.Role;
import com.network.network.security.jwt.JwtToken;
import com.network.network.user.repr.RegisterRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    @Column(unique=true)
    private String email;

    @JsonIgnore
    private String password;

    private String phoneNumber;

    @OneToOne(cascade=CascadeType.REMOVE)
    @JsonIgnore
    private Media avatar;

    @JsonIgnore
    @OneToOne(cascade=CascadeType.REMOVE)
    private Media cv;

    @ManyToOne @JsonIgnoreProperties("users")
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> liked = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    List<JwtToken> jwtTokens = new ArrayList<>();

    public User(RegisterRequest registerRequest) {
        this.firstName = registerRequest.getFirstName();
        this.lastName = registerRequest.getLastName();
        this.email = registerRequest.getEmail();
        this.password = registerRequest.getPassword();
        this.phoneNumber = registerRequest.getPhoneNumber();
    }

    public void addToken(JwtToken jwtToken) {
        jwtTokens.add(jwtToken);
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public void addLiked(Post post) {
        liked.add(post);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
