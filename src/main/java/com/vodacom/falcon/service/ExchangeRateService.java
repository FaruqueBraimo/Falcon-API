package com.vodacom.falcon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import com.vodacom.falcon.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.Map;

import static com.vodacom.falcon.util.FalconDefaults.COUNTRY_API_BASE_URL;
import static com.vodacom.falcon.util.FalconDefaults.MAIN_EXCHANGE_RATE_API_BASE_URL;
import static com.vodacom.falcon.util.FalconDefaults.OPTIONAL_EXCHANGE_RATE_API_BASE_URL;
import static com.vodacom.falcon.util.JsonUtil.deserialize;
import static com.vodacom.falcon.util.JsonUtil.deserializeByTypeReference;

@Service
public class ExchangeRateService {

    @Value("${main-exchange-rates-api.apiKey}")
    private String mainExchangeRatesApiKey;

    @Value("${optional-exchange-rates-api.apiKey}")
    private String optionalExchangeRatesApiKey;

    public ExchangeRateResponse getExchangeRates(String countryCode) {
        String currency = this.getCurrency(countryCode);
        String mainExchangeRateUrl = String.format("%s/v1/latest?symbols=%s&access_key=%s", MAIN_EXCHANGE_RATE_API_BASE_URL, currency, mainExchangeRatesApiKey); // Limited to 250 request per month on free acc.
        String optionalExchangeRateUrl = String.format("%s/v2.0/rates/latest?symbols=%s&apikey=%s", OPTIONAL_EXCHANGE_RATE_API_BASE_URL, currency, optionalExchangeRatesApiKey); // Up to 1k requests. ;

        ExchangeRateResponse ratesFromMainSource = buildExchangeRates(mainExchangeRateUrl);

        if (ratesFromMainSource != null) {
            return ratesFromMainSource;
        }
        return buildExchangeRates(optionalExchangeRateUrl);
    }

    private ExchangeRateResponse buildExchangeRates(String url) {
        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            return deserialize(response.body(), ExchangeRateResponse.class);
        }
        return null;
    }

    private String getCurrency(String countyCode) {
        String url = String.format("%s/api/v0.1/countries/currency/q?iso2=%s", COUNTRY_API_BASE_URL, countyCode);

        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            Map<Object, Object> data = deserializeByTypeReference(response.body(), new TypeReference<>() {
            });
            if (data != null) {
                Map<Object, Object> currency = deserializeByTypeReference(JsonUtil.serialize(data.get("data")), new TypeReference<>() {
                });
                if (currency != null) return currency.get("currency").toString();
            }
        }
        return null;
    }
}
