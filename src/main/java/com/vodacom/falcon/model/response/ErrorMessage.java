package com.vodacom.falcon.model.response;

import org.springframework.http.HttpStatus;

public record ErrorMessage(String message, HttpStatus status) {
}
