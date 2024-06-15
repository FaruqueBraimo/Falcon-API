package com.vodacom.falcon.model.response.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenLocationResponse {
    @JsonProperty("country")
    private String countryCode;
    private String lat;
    private String lon;
}