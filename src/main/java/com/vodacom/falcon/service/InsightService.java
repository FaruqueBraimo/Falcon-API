package com.vodacom.falcon.service;

import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.MetadataResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.vodacom.falcon.util.FalconDefaults.WB_FILTER_DATE;
import static com.vodacom.falcon.util.FalconDefaults.WB_RANGE_FILTER_DATE;

@Service
@Slf4j
@RequiredArgsConstructor

public class InsightService {

    private final EconomyInsightService economyInsightService;
    private final WeatherForecastService weatherForecastService;
    private final ExchangeRateService exchangeRateService;
    private final CountryMetadataService countryMetadataService;

    public InsightResponse getInsight(String city) {
        log.info("Getting insights for {}", city);
        MetadataResponse metadata = new MetadataResponse();

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        String countryCode = countryMetadataService.getCountryCode(encodedCity);
        WeatherForecastResponse weatherForecast = new WeatherForecastResponse();
        metadata.setCountry(true);

        if (Objects.isNull(countryCode)) {
            weatherForecast = weatherForecastService.getWeatherForecast(encodedCity);
            countryCode = weatherForecast
                    .getForecast()
                    .getLocation()
                    .getCountryCode();

            metadata.setCountry(false);

        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        metadata.setAuthenticatedUser(true);
        metadata.setMessage("Enjoy your destination %s! ");

        ExchangeRateResponse exchangeRateResponse = exchangeRateService.getExchangeRates(countryCode);

        if (authentication instanceof AnonymousAuthenticationToken) {
            metadata.setAuthenticatedUser(false);
            metadata.setMessage("Please login, to see Weather Forecast and Exchange Rates");
            weatherForecast = null;
            exchangeRateResponse = null;
        }

        EconomyInsightResponse economyInsight = economyInsightService.getEconomyInsight(countryCode, WB_FILTER_DATE);

        return InsightResponse
                .builder()
                .metadata(metadata)
                .economyInsight(economyInsight)
                .weatherForecast(weatherForecast)
                .exchangeRate(exchangeRateResponse)
                .build();
    }


    public HistoricalEconomyInsightResponse getHistoricalInsights(String city) {
        log.info("Getting insights for {}", city);

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String countryCode = countryMetadataService.getCountryCode(encodedCity);

        if (Objects.nonNull(countryCode)) {
            return economyInsightService.getHistoricalEconomyInsight(countryCode, WB_RANGE_FILTER_DATE);
        }
        return null;
    }
}
