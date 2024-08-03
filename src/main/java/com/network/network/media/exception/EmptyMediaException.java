package com.network.network.media.exception;

public class EmptyMediaException extends RuntimeException {
    public EmptyMediaException() {
        super("Empty media");
    }
}
