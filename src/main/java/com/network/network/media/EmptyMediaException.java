package com.network.network.media;

public class EmptyMediaException extends RuntimeException {
    public EmptyMediaException() {
        super("Empty media");
    }
}
