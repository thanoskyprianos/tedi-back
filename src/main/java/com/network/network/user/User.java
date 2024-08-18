package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
import com.network.network.misc.View;
import com.network.network.post.Post;
import com.network.network.role.Role;
import com.network.network.security.jwt.JwtToken;
import com.network.network.user.info.Info;
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
    @Id
    @JsonView(View.AsProfessional.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonView(View.AsProfessional.class)
    private String firstName;

    @JsonView(View.AsProfessional.class)
    private String lastName;

    @Column(unique=true)
    @JsonView(View.AsProfessional.class)
    private String email;

    @JsonIgnore
    private String password;

    @JsonView(View.AsProfessional.class)
    private String phoneNumber;

    @JsonView(View.AsAdmin.class)
    @OneToOne(cascade = CascadeType.REMOVE)
    private Info info;

    @JsonIgnore
    @OneToOne(cascade=CascadeType.REMOVE)
    private Media avatar;

    @ManyToOne
    @JsonView(View.AsProfessional.class)
    private Role role;

    @OneToMany
    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"connected", "posts", "liked", "comments", "info"})
    @JoinTable(name = "connections",
            joinColumns = @JoinColumn(name = "user_1"),
            inverseJoinColumns = @JoinColumn(name = "user2"))
    private Set<User> connected = new HashSet<>();

    @JsonView(View.AsAdmin.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    @JsonView(View.AsAdmin.class)
    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> liked = new HashSet<>();

    @JsonView(View.AsAdmin.class)
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

    // copy constructor
    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.info = user.getInfo();
        this.avatar = user.getAvatar();
        this.role = user.getRole();
        this.connected = new HashSet<>(user.getConnected());
        this.posts = new ArrayList<>(user.getPosts());
        this.liked = new HashSet<>(user.getLiked());
        this.comments = new ArrayList<>(user.getComments());
        this.jwtTokens = new ArrayList<>(user.getJwtTokens());
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

    public void addConnected(User user) {
        connected.add(user);
    }

    public boolean isConnected(User user) {
        return connected.contains(user);
    }
}
