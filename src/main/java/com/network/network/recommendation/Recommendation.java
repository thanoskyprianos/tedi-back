package com.network.network.recommendation;

import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Recommendation implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    private double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "`type`")
    private JobType type;
}
