package com.vodacom.falcon.model.response.workdbank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WordBankObjectResponse {
    private WBIndicatorResponse indicator;
    private WBCountryResponse country;
    private String value;
    private String date;
}
