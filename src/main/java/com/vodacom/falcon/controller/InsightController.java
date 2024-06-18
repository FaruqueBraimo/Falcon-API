package com.vodacom.falcon.controller;

import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.service.InsightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "*")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("falcon/insight")
@EnableCaching
public class InsightController {
    private final InsightService falconInsightService;

    @GetMapping()
    public ResponseEntity<InsightResponse> getInsight(@RequestParam("city") String city) {
        InsightResponse response = falconInsightService.getInsight(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/historical")
    @Cacheable(key = "#city", value = "historical")
    public ResponseEntity<HistoricalEconomyInsightResponse> getHistoricalInsights(@RequestParam("city") String city) throws InterruptedException {
        HistoricalEconomyInsightResponse response = falconInsightService.getHistoricalInsights(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
