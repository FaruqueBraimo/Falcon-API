package com.vodacom.falcon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.model.response.EconomyInsightResponse;
import com.vodacom.falcon.model.response.workdbank.WordBankObjectResponse;
import com.vodacom.falcon.util.FalconDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.List;

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
}
