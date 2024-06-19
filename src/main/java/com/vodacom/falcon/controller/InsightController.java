package com.vodacom.falcon.controller;

import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.InsightResponse;
import com.vodacom.falcon.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    @Operation(summary = "Get insight by city", description = "Should return population, gdp, exchange rates, weather forecast. The last 2 if the user is authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")})
    })
    public ResponseEntity<InsightResponse> getInsight(@RequestParam("city") @Valid String city) {
        InsightResponse response = falconInsightService.getInsight(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/historical")
    @Operation(summary = "Get historical insights by country", description = "Should return population and gdp from 2012 to 2022 for given country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")})
    })
    @Cacheable(key = "#city", value = "historical")
    public ResponseEntity<HistoricalEconomyInsightResponse> getHistoricalInsights(@RequestParam("city") @Valid String city) {
        HistoricalEconomyInsightResponse response = falconInsightService.getHistoricalInsights(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
