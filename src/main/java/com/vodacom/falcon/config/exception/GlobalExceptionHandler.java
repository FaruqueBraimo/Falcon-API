package com.vodacom.falcon.config.exception;

import com.vodacom.falcon.model.response.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorMessage> handleExistingResource(DuplicateKeyException ex) {
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleExistingResource(UsernameNotFoundException ex) {
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex) {
        String error = "Internal Server Error: Sorry! An unexpected error occurred";
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), error);
        log.error(error);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        String error = String.format("Invalid inputs %s", errors);
        ErrorMessage errorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), error);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
