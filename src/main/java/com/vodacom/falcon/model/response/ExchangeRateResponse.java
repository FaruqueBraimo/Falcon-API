package com.vodacom.falcon.model.response;

import java.util.Map;

public record ExchangeRateResponse(String date, String base, Map<String, Object> rates) {
}
