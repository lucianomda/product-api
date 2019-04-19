package com.lucianomda.rakuten.productapi.service;

import com.google.common.base.Preconditions;
import com.lucianomda.rakuten.productapi.client.fixerio.FixerIoClient;
import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

	private FixerIoClient fixerIoClient;

	@Autowired
	public CurrencyExchangeServiceImpl(FixerIoClient fixerIoClient) {
		this.fixerIoClient = fixerIoClient;
	}

	@Override
	public BigDecimal convertTo(@Size(min = 3, max = 3) String fromCurrencyCode,
						 BigDecimal fromAmount,
						 @Size(min = 3, max = 3) String toCurrencyCode) {

		LatestRatesResponse latestRatesResponse = fixerIoClient.getCurrencyExchanges(toCurrencyCode);
		Preconditions.checkArgument(latestRatesResponse.getRates().containsKey(fromCurrencyCode),
				"Currency code '%s' is not supported by currency exchange service.", fromCurrencyCode);

		BigDecimal exchangeRate = latestRatesResponse.getRates().get(fromCurrencyCode);
		return fromAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
	}
}
