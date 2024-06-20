package com.vodacom.falcon.util;

import com.vodacom.falcon.config.exception.ResourceNotFoundException;

public class ValidationFactory {
    public static void validateSearch(String term, String value) throws ResourceNotFoundException {
        boolean isValid = value != null && value.length() > 3 && value.matches("^[a-zA-Z]*$");
        if (!isValid) {
            throw new ResourceNotFoundException(String.format("Please provide a valid: %s", term));
        }
    }
}
