package com.lucianomda.rakuten.productapi.client.fixerio.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter
@EqualsAndHashCode
public class LatestRatesResponse {

	private String base;
	Map<String, BigDecimal> rates;
}
