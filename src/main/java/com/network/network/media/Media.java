package com.network.network.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.File;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    private String path;

    private String contentType;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    private Date date; // can use it for deleting orphans

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
