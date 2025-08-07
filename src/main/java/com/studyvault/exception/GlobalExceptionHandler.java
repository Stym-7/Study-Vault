package com.studyvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle RuntimeExceptions like email already exists etc.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // You can add more exception types here, e.g.:
    // @ExceptionHandler(UserNotFoundException.class)
    // public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
    //     return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    // }
}
