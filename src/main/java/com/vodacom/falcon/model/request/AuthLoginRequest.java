package com.vodacom.falcon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @NotBlank(message = "Username should not be null or empty" )
        String username,
        @NotBlank(message = "Username should not be null or empty")
        String password) {
}
