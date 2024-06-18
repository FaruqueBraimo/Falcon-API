package com.vodacom.falcon.model.response;

import com.vodacom.falcon.model.response.workdbank.GDPResponse;
import com.vodacom.falcon.model.response.workdbank.PopulationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HistoricalEconomyInsightResponse {
    Set<GDPResponse> gdp;
    Set<PopulationResponse> population;
}
