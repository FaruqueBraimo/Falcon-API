package com.vodacom.falcon.model.response;

import com.vodacom.falcon.model.response.openweathermap.OpenWeatherForecastResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class WeatherForecastResponse {
    private OpenWeatherForecastResponse forecast;
}
