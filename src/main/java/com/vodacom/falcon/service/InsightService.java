package com.vodacom.falcon.service;

import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.vodacom.falcon.util.FalconDefaults.WB_FILTER_DATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightService {

    private final EconomyInsightService economyInsightService;
    private final WeatherForecastService weatherForecastService;
    private final ExchangeRateService exchangeRateService;

    public InsightResponse getInsight(String city) {
        log.info("Getting insights for {}", city);

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        WeatherForecastResponse weatherForecast = weatherForecastService.getWeatherForecast(encodedCity);

        String countryCode = weatherForecast.getForecast().getLocation().getCountryCode();

        EconomyInsightResponse economyInsight = economyInsightService.getEconomyInsight(countryCode, WB_FILTER_DATE);
        ExchangeRateResponse exchangeRateResponse = exchangeRateService.getExchangeRates(countryCode);

        return InsightResponse
                .builder()
                .economyInsight(economyInsight)
                .weatherForecast(weatherForecast)
                .exchangeRate(exchangeRateResponse)
                .build();
    }
}
