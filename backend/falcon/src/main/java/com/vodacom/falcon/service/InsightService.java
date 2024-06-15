package com.vodacom.falcon.service;

import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightService {

    private final EconomyInsightService economyInsightService;
    private final WeatherForecastService weatherForecastService;

    public InsightResponse getInsight(String city) {
        log.info("Getting insights for {}", city);
        //FIXME: Use the provided city.

        WeatherForecastResponse weatherForecast = weatherForecastService.getWeatherForecast(city);

        EconomyInsightResponse economyInsight = economyInsightService.getEconomyInsight("mz", 2022);

        return InsightResponse
                .builder()
                .economyInsight(economyInsight)
                .weatherForecast(weatherForecast)
                .build();
    }


}
