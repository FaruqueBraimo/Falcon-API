package com.vodacom.falcon.model.response.openweathermap;

import java.util.List;

public record OpenWeatherForecastResponse(OpenCurrentWeatherResponse current, List<OpenDailyWhetherResponse> daily) {
}
