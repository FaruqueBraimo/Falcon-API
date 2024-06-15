package com.vodacom.falcon.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EconomyInsightResponse {
    private Long population;
    private BigDecimal GDP;
    private Long year;
}
