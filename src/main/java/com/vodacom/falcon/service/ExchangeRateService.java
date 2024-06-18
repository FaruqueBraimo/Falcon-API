package com.vodacom.falcon.service;

import com.vodacom.falcon.client.APICaller;
import com.vodacom.falcon.model.response.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

import static com.vodacom.falcon.util.FalconDefaults.MAIN_EXCHANGE_RATE_API_BASE_URL;
import static com.vodacom.falcon.util.FalconDefaults.OPTIONAL_EXCHANGE_RATE_API_BASE_URL;
import static com.vodacom.falcon.util.JsonUtil.deserialize;

@Service
public class ExchangeRateService {

    @Value("${main-exchange-rates-api.apiKey}")
    private String mainExchangeRatesApiKey;

    @Value("${optional-exchange-rates-api.apiKey}")
    private String optionalExchangeRatesApiKey;

    @Autowired
    private CountryMetadataService countryMetadataService;

    @Cacheable(key = "#countryCode", value = "rates")
    public ExchangeRateResponse getExchangeRates(String countryCode) {
        String currency = this.countryMetadataService.getCurrency(countryCode);
        String mainExchangeRateUrl = String.format("%s/v1/latest?symbols=%s&access_key=%s", MAIN_EXCHANGE_RATE_API_BASE_URL, currency, mainExchangeRatesApiKey); // Limited to 250 request per month on free acc.
        String optionalExchangeRateUrl = String.format("%s/v2.0/rates/latest?symbols=%s&apikey=%s", OPTIONAL_EXCHANGE_RATE_API_BASE_URL, currency, optionalExchangeRatesApiKey); // Up to 1k requests. ;

        ExchangeRateResponse ratesFromMainSource = buildExchangeRates(mainExchangeRateUrl);


//        FixME: Enable this
//        if (ratesFromMainSource != null) {
//            return ratesFromMainSource;
//        }
        return buildExchangeRates(optionalExchangeRateUrl);
    }

    private ExchangeRateResponse buildExchangeRates(String url) {
        HttpResponse<String> response = APICaller.getData(url);
        if (response != null) {
            return deserialize(response.body(), ExchangeRateResponse.class);
        }
        return null;
    }
}
