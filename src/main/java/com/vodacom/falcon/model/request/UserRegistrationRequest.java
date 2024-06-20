package com.vodacom.falcon.model.request;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(@NotBlank(message = "Username should not be null or empty") String username,
                                      @NotBlank(message = "Username should not be null or empty") String password) {
}
