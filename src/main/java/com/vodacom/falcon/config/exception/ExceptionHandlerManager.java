package com.vodacom.falcon.config.exception;

import com.vodacom.falcon.model.response.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestControllerAdvice
public class ExceptionHandlerManager extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerManager.class);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String message = "Error. %s - %s";
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    protected ResponseEntity<ErrorMessage> handleException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        final String DEFAULT_MESSAGE = "Malformed JSON request";
        final String INVALID_CREDENTIALS = "Invalid credentials";
        String error = ex.getMessage();

        if (ex.getMessage().contains(INVALID_CREDENTIALS)) {
            error = INVALID_CREDENTIALS;
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.BAD_REQUEST;
            error = Objects.nonNull(error) ? error.contains("body is missing") ? "Required request body is missing" : error : DEFAULT_MESSAGE;
        }
        ErrorMessage response = returnMessage(error, ex);
        return new  ResponseEntity<>(response, response.status());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    protected ResponseEntity<ErrorMessage> handleNullPointerException(NullPointerException ex) {
        status = HttpStatus.NO_CONTENT;
        ErrorMessage response = returnMessage("Null Pointer Exception", ex);
        return new  ResponseEntity<>(response, response.status());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ResponseEntity<ErrorMessage> handleException(Exception ex) {
        if (ex instanceof SQLException && (ex.getMessage().contains("SQLGrammarException") || ex.getMessage().contains("ConstraintViolationException"))) {
            ErrorMessage response = returnMessage("SQLGrammarException", ex);
            return new  ResponseEntity<>(response, response.status());
        }
        ErrorMessage response = returnMessage("Exception", ex);
        return new  ResponseEntity<>(response, response.status());
    }


    @ExceptionHandler(IOException.class)
    @ResponseBody
    protected ResponseEntity<ErrorMessage> handleIOException(IOException ex) {
        ErrorMessage response = returnMessage("IO Exception", ex);
        return new  ResponseEntity<>(response, response.status());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    protected ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException ex){
        ErrorMessage response = returnMessage("RuntimeException", ex);
        return new  ResponseEntity<>(response, response.status());
    }

    private ErrorMessage returnMessage(String error, Exception ex){
        message = String.format(message, error, ex.getMessage());
        LOGGER.error(message);
        return new ErrorMessage(message, status);
    }
}
