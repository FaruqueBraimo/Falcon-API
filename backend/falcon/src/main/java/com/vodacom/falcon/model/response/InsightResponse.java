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
    private String country;
    private String city;
    private EconomyInsightResponse economyInsight;
    private double exchangeRate;
    private WeatherForecastResponse weatherForecast;
}
