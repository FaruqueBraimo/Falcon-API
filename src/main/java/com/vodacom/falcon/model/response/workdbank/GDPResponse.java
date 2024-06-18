package com.vodacom.falcon.model.response.workdbank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GDPResponse {
    private Long year;
    private BigDecimal value;
}
