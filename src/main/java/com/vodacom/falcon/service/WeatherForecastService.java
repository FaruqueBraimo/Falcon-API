package com.vodacom.falcon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.config.exception.ResourceNotFoundException;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import com.vodacom.falcon.model.response.openweathermap.OpenLocationResponse;
import com.vodacom.falcon.model.response.openweathermap.OpenWeatherForecastResponse;
import com.vodacom.falcon.util.FalconDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

import static com.vodacom.falcon.util.JsonUtil.deserialize;
import static com.vodacom.falcon.util.JsonUtil.deserializeByTypeReference;
import static com.vodacom.falcon.util.JsonUtil.serialize;

@Service
@Slf4j
public class WeatherForecastService {
    @Value("${open-weather-map.apiKeyV2}")
    private String openWeatherApiKeyV2;

    @Value("${open-weather-map.apiKeyV3}")
    private String openWeatherApiKeyV3;

    private final String OPEN_WEATHER_API_VERSION = "3.0"; // TODO: Add feature flag Or row controller, to determine the version to use. (2.5 0r 3.0)

    public WeatherForecastResponse getWeatherForecast(String city) throws ResourceNotFoundException {
        return WeatherForecastResponse
                .builder()
                .forecast(buildWeatherForecast(city))
                .build();
    }

    private OpenWeatherForecastResponse buildWeatherForecast(String city) throws ResourceNotFoundException {
        OpenLocationResponse location = this.getLocation(city);

        if (location == null) {
            log.info("Couldn't find location for {}", city);
            throw new ResourceNotFoundException(String.format("City: %s Not found", city));
        }

        String url = String.format("%s/data/%s/onecall?units=metric&cnt=4&exclude=hourly,minutely,alerts&lat=%s&lon=%s&appid=%s", FalconDefaults.OPEN_WEATHER_API_BASE_URL, OPEN_WEATHER_API_VERSION, location.getLat(), location.getLon(), openWeatherApiKeyV3);
        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            OpenWeatherForecastResponse openWeatherForecast = deserialize(response.body(), OpenWeatherForecastResponse.class);
            if (openWeatherForecast != null) {
                openWeatherForecast.setLocation(location);
                return openWeatherForecast;
            }
        }
        return null;
    }

    private OpenLocationResponse getLocation(String city) {
        String url = String.format("%s/geo/1.0/direct?limit=1&q=%s&appid=%s", FalconDefaults.OPEN_WEATHER_API_BASE_URL, city, openWeatherApiKeyV3);

        HttpResponse<String> response = APICaller.getData(url);
        if (response != null && !response.body().equals("[]")) {
            Object[] object = deserialize(response.body(), Object[].class);
            if (object != null) {
                return object[0] != null ? deserializeByTypeReference(serialize(object[0]), new TypeReference<>() {
                }) : null;
            }
        }
        return null;
    }
}
