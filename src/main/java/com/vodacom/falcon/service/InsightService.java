package com.vodacom.falcon.service;

import com.vodacom.falcon.config.exception.ResourceNotFoundException;
import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.MetadataResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import com.vodacom.falcon.util.ValidationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public InsightResponse getInsight(String city) throws ResourceNotFoundException, ExecutionException, InterruptedException {
        log.info("Getting insights for {}", city);
        ValidationFactory.validateSearch("City", city);

        MetadataResponse metadata = new MetadataResponse();

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        String countryCode = null;

        CompletableFuture<String> countryTaskResponse = CompletableFuture
                .supplyAsync(() -> countryMetadataService.getCountryCode(encodedCity))
                .exceptionally(
                        (e) -> {
                            log.error("Error getting country data ");
                            throw new RuntimeException(e.getMessage());
                        });

        CompletableFuture<WeatherForecastResponse> weatherForecastTaskResponse = CompletableFuture
                .supplyAsync(() -> weatherForecastService.getWeatherForecast(encodedCity))
                .exceptionally(
                        (e) -> {
                            log.error("Error getting weather ");
                            throw new RuntimeException(e.getMessage());
                        });

        CompletableFuture<Void> combinedCountryFuture = CompletableFuture.allOf(countryTaskResponse, weatherForecastTaskResponse);

        combinedCountryFuture.join();

        WeatherForecastResponse weatherForecast = new WeatherForecastResponse();
        metadata.setCountry(true);

        countryCode = countryTaskResponse.get();

        if (Objects.isNull(countryCode)) {
            weatherForecast = weatherForecastTaskResponse.get();
            countryCode = weatherForecast
                    .getForecast()
                    .getLocation()
                    .getCountryCode();

            metadata.setCountry(false);
        }

        if (countryCode == null) {
            throw new ResourceNotFoundException(String.format("City: %s Not found", city));
        }

        String finalCountryCode = countryCode;
        CompletableFuture<EconomyInsightResponse> economyTaskResponse = CompletableFuture
                .supplyAsync(() -> economyInsightService.getEconomyInsight(finalCountryCode, WB_FILTER_DATE))
                .exceptionally(
                        (e) -> {
                            log.error("Error getting economy data ");
                            throw new RuntimeException(e.getMessage());
                        });

        CompletableFuture<ExchangeRateResponse> exchangeRateTaskResponse = CompletableFuture
                .supplyAsync(() -> exchangeRateService.getExchangeRates(finalCountryCode))
                .exceptionally(
                        (e) -> {
                            log.error("Error getting exchanges ");
                            throw new RuntimeException(e.getMessage());
                        }
                );

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(economyTaskResponse, exchangeRateTaskResponse);

        combinedFuture.join();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        metadata.setAuthenticatedUser(true);
        metadata.setMessage("Enjoy your destination %s! ");

        ExchangeRateResponse exchangeRateResponse = exchangeRateTaskResponse.get();

        if (authentication instanceof AnonymousAuthenticationToken) {
            metadata.setAuthenticatedUser(false);
            metadata.setMessage("Please login, to see Weather Forecast and Exchange Rates");
            weatherForecast = null;
            exchangeRateResponse = null;
        }

        EconomyInsightResponse economyInsight = economyTaskResponse.get();

        return InsightResponse
                .builder()
                .metadata(metadata)
                .economyInsight(economyInsight)
                .weatherForecast(weatherForecast)
                .exchangeRate(exchangeRateResponse)
                .build();
    }


    public HistoricalEconomyInsightResponse getHistoricalInsights(String country) throws ResourceNotFoundException {
        log.info("Getting insights for {}", country);
        ValidationFactory.validateSearch("Country", country);

        String encodedCity = URLEncoder.encode(country, StandardCharsets.UTF_8);
        String countryCode = countryMetadataService.getCountryCode(encodedCity);

        if (Objects.nonNull(countryCode)) {
            return economyInsightService.getHistoricalEconomyInsight(countryCode, WB_RANGE_FILTER_DATE);
        }
        throw new ResourceNotFoundException(String.format("Country: %s Not found", country));

    }


}
