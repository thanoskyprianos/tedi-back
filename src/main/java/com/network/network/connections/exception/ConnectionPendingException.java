package com.network.network.connections.exception;

public class ConnectionPendingException extends RuntimeException {
    public ConnectionPendingException() {
        super("Connection already pending.");
    }
}
