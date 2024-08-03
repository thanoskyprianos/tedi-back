package com.network.network.media.exception;

import lombok.Getter;

@Getter
public class MediaNotFoundException extends RuntimeException {
    private final int id;

    public MediaNotFoundException(int id) {
        super("Media with id: " + id + " not found");
        this.id = id;
    }
}
