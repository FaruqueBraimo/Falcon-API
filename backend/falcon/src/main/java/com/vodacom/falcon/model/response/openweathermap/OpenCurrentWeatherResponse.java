package com.vodacom.falcon.model.response.openweathermap;

import java.util.List;

public record OpenCurrentWeatherResponse(String dt, String sunrise, String sunset,
                                         String temp, String humidity, String pressure,
                                         String wind_speed, List<OpenCurrentWeatherDescResponse> weather) {
}
