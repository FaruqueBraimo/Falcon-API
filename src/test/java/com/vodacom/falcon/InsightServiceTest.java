package com.vodacom.falcon;

import com.vodacom.falcon.config.exception.ResourceNotFoundException;
import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.model.response.WeatherForecastResponse;
import com.vodacom.falcon.model.response.openweathermap.OpenCurrentWeatherResponse;
import com.vodacom.falcon.model.response.openweathermap.OpenLocationResponse;
import com.vodacom.falcon.model.response.openweathermap.OpenWeatherForecastResponse;
import com.vodacom.falcon.service.CountryMetadataService;
import com.vodacom.falcon.service.EconomyInsightService;
import com.vodacom.falcon.service.ExchangeRateService;
import com.vodacom.falcon.service.InsightService;
import com.vodacom.falcon.service.WeatherForecastService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InsightServiceTest {
    @InjectMocks
    private InsightService insightService;

    @Mock
    private EconomyInsightService economyInsightService;

    @Mock
    private WeatherForecastService weatherForecastService;

    @Mock
    private CountryMetadataService countryMetadataService;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Spy
    private WeatherForecastResponse weatherForecastResponse;
    private EconomyInsightResponse economyInsightResponse;
    private ExchangeRateResponse exchangeRateResponse;

    @Before("Running before")
    public void setup() {
        OpenWeatherForecastResponse openWeatherForecastResponse = new OpenWeatherForecastResponse();
        OpenLocationResponse locationResponse = new OpenLocationResponse("mz", "123", "1245");

        openWeatherForecastResponse.setLocation(locationResponse);
        openWeatherForecastResponse.setDaily(new ArrayList<>());
        openWeatherForecastResponse.setCurrent(new OpenCurrentWeatherResponse(
                "111", "123", "1234", "10.3", "11", "100", "11", new ArrayList<>()

        ));
        weatherForecastResponse = new WeatherForecastResponse(openWeatherForecastResponse);

        economyInsightResponse = new EconomyInsightResponse("Moz", 1L, BigDecimal.ONE, 2021L);

        exchangeRateResponse = new ExchangeRateResponse("2022", "EUR", new HashMap<>());
    }


    @Test
    @DisplayName("JUnit test for insights on country")
    public void shouldReturnOnlyNotReturnWeatherWhenTheSearchIsCountry() {
        setup();
        when(countryMetadataService.getCountryCode(any())).thenReturn("mz");
        when(weatherForecastService.getWeatherForecast(any())).thenReturn(null);
        when(economyInsightService.getEconomyInsight(any(), any())).thenReturn(economyInsightResponse);
        when(exchangeRateService.getExchangeRates(any())).thenReturn(exchangeRateResponse);
        try {
            InsightResponse insightResponse = insightService.getInsight("mozambique");
            System.out.println(insightResponse);
            verify(countryMetadataService, times(1)).getCountryCode(any());
            verify(weatherForecastService, times(1)).getWeatherForecast(any());
            verify(exchangeRateService, times(1)).getExchangeRates(any());
            System.out.println(insightResponse);

            assertThat(insightResponse.getWeatherForecast().getForecast()).isNull();
            assertThat(insightResponse.getEconomyInsight()).isNotNull();
            assertThat(insightResponse.getExchangeRate()).isNotNull();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("JUnit test for insights on city")
    public void shouldReturnAllWhenTheSearchIsCity() {
        setup();
        when(countryMetadataService.getCountryCode(any())).thenReturn(null);
        when(weatherForecastService.getWeatherForecast(any())).thenReturn(weatherForecastResponse);
        when(economyInsightService.getEconomyInsight(any(), any())).thenReturn(economyInsightResponse);
        when(exchangeRateService.getExchangeRates(any())).thenReturn(exchangeRateResponse);
        try {
            InsightResponse insightResponse = insightService.getInsight("maputo");
            System.out.println(insightResponse);
            verify(countryMetadataService, times(1)).getCountryCode(any());
            verify(weatherForecastService, times(1)).getWeatherForecast(any());
            verify(exchangeRateService, times(1)).getExchangeRates(any());
            System.out.println(insightResponse);

            assertThat(insightResponse.getWeatherForecast().getForecast()).isNotNull();
            assertThat(insightResponse.getEconomyInsight()).isNotNull();
            assertThat(insightResponse.getExchangeRate()).isNotNull();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("JUnit test for insights on city")
    public void shouldThrowExceptionWhenTheCityIsNotRecognized() {
        setup();
        when(countryMetadataService.getCountryCode(any())).thenReturn(null);
        when(weatherForecastService.getWeatherForecast(any())).thenReturn(null);
        try {
            assertThrows(ResourceNotFoundException.class, () -> {
                insightService.getInsight("aaaaaaaaa");
            });

            verify(countryMetadataService, times(1)).getCountryCode(any());
            verify(weatherForecastService, times(1)).getWeatherForecast(any());
            verify(exchangeRateService, times(0)).getExchangeRates(any());
            verify(economyInsightService, times(0)).getEconomyInsight(any(), any());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
