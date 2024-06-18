package com.vodacom.falcon.model.response.openweathermap;

import java.util.List;

public record OpenDailyWhetherResponse(String dt, String sunrise, String sunset,
                                       String summary, List<OpenCurrentWeatherDescResponse> weather,
                                       OpenDailyTemperatureWeatherResponse temp) {
}
