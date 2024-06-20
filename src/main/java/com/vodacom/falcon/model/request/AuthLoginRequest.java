package com.vodacom.falcon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @NotBlank(message = "Username should not be null or empty") @Pattern(regexp = "[a-zA-Z0-9 ]")
        String username,
        @NotBlank(message = "Username should not be null or empty") @Size(min = 6, max = 64)
        String password) {
}
