package com.network.network.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.network.network.media.Media;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;

    @JsonIgnore
    @JoinTable(name = "post_media")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Media> mediaList = new ArrayList<>();

    @ManyToOne @JsonIgnore
    private User user;

    public Post(String text) { this.text = text; }

    public void addMedia(Media media) {
        mediaList.add(media);
    }
}
