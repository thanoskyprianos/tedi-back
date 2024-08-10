package com.network.network.comment.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(int id) {
        super("Could not find comment with id " + id);
    }
}
