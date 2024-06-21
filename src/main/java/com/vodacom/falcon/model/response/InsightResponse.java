package com.vodacom.falcon.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class InsightResponse {
    private MetadataResponse metadata;
    private EconomyInsightResponse economyInsight;
    private ExchangeRateResponse exchangeRate;
    private WeatherForecastResponse weatherForecast;
}
