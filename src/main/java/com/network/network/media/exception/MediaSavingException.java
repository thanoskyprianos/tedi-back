package com.network.network.media.exception;

import lombok.Getter;

@Getter
public class MediaSavingException extends RuntimeException {
    private final String fileName;

    public MediaSavingException(String fileName) {
        super("Error while saving media: " + fileName);
        this.fileName = fileName;
    }
}
