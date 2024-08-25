package com.network.network.misc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
public class MiscAdvice {

    @ExceptionHandler({
            NoResourceFoundException.class,
            FileNotFoundException.class
    })
    public ResponseEntity<?> handleNoResourceFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class,
            HttpMessageNotReadableException.class })
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
