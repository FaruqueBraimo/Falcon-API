package com.vodacom.falcon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.Map;

import static com.vodacom.falcon.util.FalconDefaults.COUNTRY_API_BASE_URL;
import static com.vodacom.falcon.util.JsonUtil.deserializeByTypeReference;

@Service
public class CountryMetadataService {
    public String getCountryCode(String term) {
        String url = String.format("%s/api/v0.1/countries/positions/q?country=%s", COUNTRY_API_BASE_URL, term.toLowerCase());

        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            Map<Object, Object> data = deserializeByTypeReference(response.body(), new TypeReference<>() {
            });
            if (data != null) {
                Map<Object, Object> currency = deserializeByTypeReference(JsonUtil.serialize(data.get("data")), new TypeReference<>() {
                });
                if (currency != null) return currency.get("iso2").toString();
            }
        }
        return null;
    }

    public String getCurrency(String countyCode) {
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
