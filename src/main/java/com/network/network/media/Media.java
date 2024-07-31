package com.network.network.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    private String path;

    private String contentType;

    public Media(String path, String contentType) {
        this.path = path;
        this.contentType = contentType;
    }

    public void replaceMedia(String newPath, String newContentType) {
        removeMedia(); // delete old media

        this.path = newPath;
        this.contentType = newContentType;
    }

    @PostRemove
    public void removeMedia() {
        File file = new File(this.path);
        if (file.exists()) {
            file.delete();
        }
    }
}
