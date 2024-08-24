package com.network.network.connections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ConnectionAdvice {
    @ExceptionHandler(ConnectionPendingException.class)
    public ResponseEntity<?> handleConnectionPendingException(final ConnectionPendingException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}
