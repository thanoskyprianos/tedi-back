package com.network.network.post.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(int id) {
        super("Could not find post with id " + id);
    }
}
