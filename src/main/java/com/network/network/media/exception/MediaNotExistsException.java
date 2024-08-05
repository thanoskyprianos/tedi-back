package com.network.network.media.exception;

public class MediaNotExistsException extends RuntimeException {
    public MediaNotExistsException(int index) {
        super("media at index " + index + " does not exist");
    }
}
