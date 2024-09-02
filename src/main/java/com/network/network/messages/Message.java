package com.network.network.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JsonView(View.AsProfessional.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "connected"})
    private User sender;

    @ManyToOne
    @JsonView(View.AsProfessional.class)
    @JsonIgnoreProperties({"comments", "posts", "liked", "info", "connected"})
    private User recipient;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Message(String message, User sender, User recipient) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }
}
