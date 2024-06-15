package com.vodacom.falcon.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InsightResponse {
    private EconomyInsightResponse economyInsight;
    private double exchangeRate;
    private WeatherForecastResponse weatherForecast;
}
