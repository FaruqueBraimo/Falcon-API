package com.vodacom.falcon.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataResponse {
    private boolean isAuthenticatedUser;
    private boolean isCountry;
    private String message;
}
