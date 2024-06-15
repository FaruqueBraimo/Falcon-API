package com.vodacom.falcon.model.response.openweathermap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenWeatherForecastResponse {
    private OpenLocationResponse location;
    private OpenCurrentWeatherResponse current;
    private List<OpenDailyWhetherResponse> daily;
}
