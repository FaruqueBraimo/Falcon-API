package com.vodacom.falcon.service;

import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightService {

    private final EconomyInsightService economyInsightService;
    private final WeatherForecastService weatherForecastService;

    public InsightResponse getInsight(String city) {
        log.info("Getting insights for {}", city);

        String encodeCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        WeatherForecastResponse weatherForecast = weatherForecastService.getWeatherForecast(encodeCity);

        String countryCode = weatherForecast.getForecast().getLocation().getCountryCode();

        EconomyInsightResponse economyInsight = economyInsightService.getEconomyInsight(countryCode, 2022);

        return InsightResponse
                .builder()
                .economyInsight(economyInsight)
                .weatherForecast(weatherForecast)
                .build();
    }

}
