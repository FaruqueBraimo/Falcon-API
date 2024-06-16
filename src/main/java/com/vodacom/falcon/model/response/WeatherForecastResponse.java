package com.vodacom.falcon.model.response;

import com.vodacom.falcon.model.response.openweathermap.OpenWeatherForecastResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WeatherForecastResponse {
    private OpenWeatherForecastResponse forecast;
}
