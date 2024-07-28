package com.network.network.post;

import com.network.network.media.Media;
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

    @OneToMany
    private List<Media> mediaPaths;

    public Post(String text) {
        this.text = text;

        this.mediaPaths = new ArrayList<>();
    }

//    public Post(String text, List<Media> mediaPaths) {
//        this.text = text;
//        this.mediaPaths = mediaPaths;
//    }

    public void addMedia(Media media) {
        mediaPaths.add(media);
    }
}
