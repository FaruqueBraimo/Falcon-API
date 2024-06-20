package com.vodacom.falcon.controller;

import com.vodacom.falcon.config.exception.ResourceNotFoundException;
import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(value = "*")
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class InsightController {
    private final InsightService falconInsightService;

    @GetMapping("falcon/insight")
    @Operation(summary = "Get insight by city", description = "Should return population, gdp exchange rates and weather forecast if a city is provided, if it's country only population and gdp. Full data only if the user is authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When the information is returned"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When the city is not valid or found"),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When unknown error happens")
    })
    public ResponseEntity<InsightResponse> getInsight(@RequestParam("city") String city) throws ResourceNotFoundException, ExecutionException, InterruptedException {
        InsightResponse response = falconInsightService.getInsight(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("falcon/insights/historical")
    @Operation(summary = "Get historical insights by country", description = "Should return population and gdp from 2012 to 2022 for given country. The endpoint is cached, the subsequent requests should be faster ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When the information is returned"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When the country is not valid or found"),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}, description = "When unknown error happens")
    })
    @Cacheable(key = "#city", value = "historical")
    public ResponseEntity<HistoricalEconomyInsightResponse> getHistoricalInsights(@RequestParam("city") String city) throws ResourceNotFoundException {
        HistoricalEconomyInsightResponse response = falconInsightService.getHistoricalInsights(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
