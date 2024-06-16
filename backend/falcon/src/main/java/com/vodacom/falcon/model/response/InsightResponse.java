package com.vodacom.falcon.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InsightResponse {
    private EconomyInsightResponse economyInsight;
    private ExchangeRateResponse exchangeRate;
    private WeatherForecastResponse weatherForecast;
}
