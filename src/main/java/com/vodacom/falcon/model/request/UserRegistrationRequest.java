package com.vodacom.falcon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(@NotBlank(message = "Username should not be null or empty") String username,
                                      @NotBlank(message = "Username should not be null or empty") @Size(min = 6, max = 64) String password) {
}
