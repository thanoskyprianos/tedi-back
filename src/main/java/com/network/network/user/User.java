package com.network.network.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.media.Media;
import com.network.network.messages.Message;
import com.network.network.misc.View;
import com.network.network.connections.ConnectionNotification;
import com.network.network.notification.modules.CommentNotification;
import com.network.network.notification.modules.InterestNotification;
import com.network.network.notification.modules.LikeNotification;
import com.network.network.post.Post;
import com.network.network.role.Role;
import com.network.network.security.jwt.JwtToken;
import com.network.network.user.info.Info;
import com.network.network.user.repr.RegisterRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString
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

    // JsonIgnore was getting ignored by ObjectMapper
    // when reading mock users, so we replace it with an
    // Inaccessible view to fix this issue
    @JsonView(View.Inaccessible.class)
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonView(View.AsAdmin.class)
    @JsonIgnoreProperties({"connected", "posts", "liked", "comments", "info"})
    @JoinTable(name = "connections",
            joinColumns = @JoinColumn(name = "user_1"),
            inverseJoinColumns = @JoinColumn(name = "user2"))
    private Set<User> connected = new HashSet<>();

    @JsonView(View.AsAdmin.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("user")
    private List<Post> posts = new ArrayList<>();

    @JsonView(View.AsAdmin.class)
    @ManyToMany(mappedBy = "likedBy")
    @JsonIgnoreProperties({"likedBy", "comments"})
    private Set<Post> liked = new HashSet<>();

    @JsonView(View.AsAdmin.class)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("user")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<ConnectionNotification> requests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<ConnectionNotification> received = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender",cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Message> sentMessages = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Message> receivedMessages = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<LikeNotification> sentLikeNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<LikeNotification> receivedLikeNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<CommentNotification> sentCommentNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<CommentNotification> receivedCommentNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<InterestNotification> sentInterestNotifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<InterestNotification> receivedInterestNotifications = new ArrayList<>();

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
        this.requests = new ArrayList<>(user.getRequests());
        this.received = new ArrayList<>(user.getReceived());
        this.jwtTokens = new ArrayList<>(user.getJwtTokens());
        this.sentMessages = new ArrayList<>(user.getSentMessages());
        this.receivedMessages = new ArrayList<>(user.getReceivedMessages());
        this.sentLikeNotifications = new ArrayList<>(user.getSentLikeNotifications());
        this.receivedLikeNotifications = new ArrayList<>(user.getReceivedLikeNotifications());
        this.sentCommentNotifications = new ArrayList<>(user.getSentCommentNotifications());
        this.receivedCommentNotifications = new ArrayList<>(user.getReceivedCommentNotifications());
        this.sentInterestNotifications = new ArrayList<>(user.getSentInterestNotifications());
        this.receivedInterestNotifications = new ArrayList<>(user.getReceivedInterestNotifications());
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

    public void addRequest(ConnectionNotification notification) {
        requests.add(notification);
    }

    public void addReceived(ConnectionNotification notification) {
        received.add(notification);
    }

    public boolean isConnected(User user) {
        return connected.contains(user);
    }

    public void addSentMessage(Message message) {
        sentMessages.add(message);
    }

    public void addReceivedMessage(Message message) {
        receivedMessages.add(message);
    }

    public void addSentLikeNotification(LikeNotification notification) {
        sentLikeNotifications.add(notification);
    }

    public void addReceivedLikeNotification(LikeNotification notification) {
        receivedLikeNotifications.add(notification);
    }

    public void addSentCommentNotification(CommentNotification notification) {
        sentCommentNotifications.add(notification);
    }

    public void addReceivedCommentNotification(CommentNotification notification) {
        receivedCommentNotifications.add(notification);
    }

    public void addSentInterestNotification(InterestNotification notification) {
        sentInterestNotifications.add(notification);
    }

    public void addReceivedInterestNotification(InterestNotification notification) {
        receivedInterestNotifications.add(notification);
    }
}
