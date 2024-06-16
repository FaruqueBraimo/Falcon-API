package com.vodacom.falcon.model.response.openweathermap;

public record OpenDailyWhetherResponse(String dt, String sunrise, String sunset,
                                       String summary, OpenDailyTemperatureWeatherResponse temp) {
}
