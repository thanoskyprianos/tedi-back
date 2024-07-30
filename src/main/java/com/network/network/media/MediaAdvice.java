package com.network.network.media;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MediaAdvice {
    @ExceptionHandler(MediaSavingException.class)
    public ResponseEntity<Map<String, Object>> handleMediaSavingException(MediaSavingException ex) {
        return ResponseEntity.internalServerError().body(
                Map.of(
                        "message", ex.getMessage(),
                        "fileName", ex.getFileName()
                )
        );
    }

    @ExceptionHandler(EmptyMediaException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyMediaException(EmptyMediaException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMediaNotFoundException(MediaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "message", ex.getMessage(),
                        "id", ex.getId()
                )
        );
    }
}
