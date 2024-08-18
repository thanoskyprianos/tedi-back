package com.network.network.user.info.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class InfoAdvice {
    @ExceptionHandler(InfoNotSetException.class)
    public ResponseEntity<?> handleInfoNotSetException(InfoNotSetException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
