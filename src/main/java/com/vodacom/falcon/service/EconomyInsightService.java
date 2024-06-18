package com.vodacom.falcon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.HistoricalEconomyInsightResponse;
import com.vodacom.falcon.model.response.workdbank.GDPResponse;
import com.vodacom.falcon.model.response.workdbank.PopulationResponse;
import com.vodacom.falcon.model.response.workdbank.WordBankObjectResponse;
import com.vodacom.falcon.util.FalconDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.vodacom.falcon.util.FalconDefaults.WB_GDP_INDICATOR_PARAM;
import static com.vodacom.falcon.util.FalconDefaults.WB_POPULATION_INDICATOR_PARAM;
import static com.vodacom.falcon.util.JsonUtil.deserialize;
import static com.vodacom.falcon.util.JsonUtil.deserializeByTypeReference;
import static com.vodacom.falcon.util.JsonUtil.serialize;

@Service
@Slf4j
public class EconomyInsightService {

    public EconomyInsightResponse getEconomyInsight(String countryCode, Integer date) {
        String url = String.format("%s/v2/country/%s/indicator/%s;%s/?source=2&date=%s&format=json", FalconDefaults.WORD_BANK_API_BASE_URL, countryCode.toLowerCase(), WB_POPULATION_INDICATOR_PARAM, FalconDefaults.WB_GDP_INDICATOR_PARAM, date);
        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            Object[] object = deserialize(response.body(), Object[].class);

            if (object != null && object[1] != null) {
                List<WordBankObjectResponse> wordBankData = deserializeByTypeReference(serialize(object[1]), new TypeReference<>() {
                });

                EconomyInsightResponse economyInsightResponse = new EconomyInsightResponse();
                if (wordBankData != null) {
                    wordBankData.forEach(f -> {
                                switch (f.getIndicator().id()) {
                                    case WB_POPULATION_INDICATOR_PARAM -> {
                                        economyInsightResponse.setPopulation(Long.valueOf(f.getValue()));
                                        economyInsightResponse.setYear(Long.valueOf(f.getDate()));
                                        economyInsightResponse.setCountry(f.getCountry().value());
                                    }
                                    case WB_GDP_INDICATOR_PARAM -> economyInsightResponse.setGDP(new BigDecimal(f.getValue()));
                                    default -> log.info("Unrecognized indicator {}", f.getIndicator());
                                }
                            }
                    );
                    return economyInsightResponse;
                }
            }
        }
        return null;
    }

    public HistoricalEconomyInsightResponse getHistoricalEconomyInsight(String countryCode, String date) {
        String url = String.format("%s/v2/country/%s/indicator/%s;%s/?source=2&date=%s&format=json", FalconDefaults.WORD_BANK_API_BASE_URL, countryCode.toLowerCase(), WB_POPULATION_INDICATOR_PARAM, FalconDefaults.WB_GDP_INDICATOR_PARAM, date);
        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            Object[] object = deserialize(response.body(), Object[].class);
            if (object != null) {
                List<WordBankObjectResponse> wordBankData = deserializeByTypeReference(serialize(object[1]), new TypeReference<>() {
                });
                if (wordBankData != null) {
                    return extractHistoricalInsights(wordBankData);
                }
            }
        }
        return null;
    }

    private HistoricalEconomyInsightResponse extractHistoricalInsights(List<WordBankObjectResponse> wordBankData) {
        Set<GDPResponse> gdpResponseList = new TreeSet<>(Comparator.comparing(GDPResponse::getYear));
        Set<PopulationResponse> populationResponseList = new TreeSet<>(Comparator.comparing(PopulationResponse::getYear));
        HistoricalEconomyInsightResponse history = new HistoricalEconomyInsightResponse();
        wordBankData.forEach(f -> {
                    GDPResponse gdpResponse = new GDPResponse();
                    PopulationResponse populationResponse = new PopulationResponse();

                    switch (f.getIndicator().id()) {
                        case WB_POPULATION_INDICATOR_PARAM -> {
                            populationResponse.setValue(Long.valueOf(f.getValue()));
                            populationResponse.setYear(Long.valueOf(f.getDate()));
                            populationResponseList.add(populationResponse);
                        }
                        case WB_GDP_INDICATOR_PARAM -> {
                            gdpResponse.setYear(Long.valueOf(f.getDate()));
                            gdpResponse.setValue(new BigDecimal(f.getValue()));
                            gdpResponseList.add(gdpResponse);
                        }
                        default -> log.info("Unrecognized indicator {}", f.getIndicator());
                    }
                }
        );

        history.setPopulation(populationResponseList);
        history.setGdp(gdpResponseList);
        return history;
    }
}
