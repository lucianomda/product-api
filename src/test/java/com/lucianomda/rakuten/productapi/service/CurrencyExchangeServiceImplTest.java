package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.client.fixerio.FixerIoClient;
import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class CurrencyExchangeServiceImplTest {

	@Mock
	private FixerIoClient fixerIoClient;
	@InjectMocks
	private CurrencyExchangeServiceImpl currencyExchangeService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void convertTo_success() {
		String fromCurrency = "ARS";
		BigDecimal amount = new BigDecimal("46.956363");
		String toCurrency = "EUR";
		LatestRatesResponse latestRatesResponse = createLatestRatesResponse(toCurrency);

		when(fixerIoClient.getCurrencyExchanges(toCurrency)).thenReturn(latestRatesResponse);

		BigDecimal result = currencyExchangeService.convertTo(fromCurrency, amount, toCurrency);
		assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
	}

	@Test
	public void convertTo_invalidCurrencyCode() {
		String fromCurrency = "LLL";
		BigDecimal amount = new BigDecimal("46.956363");
		String toCurrency = "EUR";
		LatestRatesResponse latestRatesResponse = createLatestRatesResponse(toCurrency);

		when(fixerIoClient.getCurrencyExchanges(toCurrency)).thenReturn(latestRatesResponse);

		assertThatThrownBy(() -> currencyExchangeService.convertTo(fromCurrency, amount, toCurrency))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Currency code 'LLL' is not supported by currency exchange service.");
	}

	@Test
	public void convertTo_exceptionOnClient() {
		String fromCurrency = "LLL";
		BigDecimal amount = new BigDecimal("46.956363");
		String toCurrency = "EUR";

		RuntimeException expectedException = new RuntimeException("Test exception on client.");
		when(fixerIoClient.getCurrencyExchanges(toCurrency)).thenThrow(expectedException);

		assertThatThrownBy(() -> currencyExchangeService.convertTo(fromCurrency, amount, toCurrency))
				.isEqualTo(expectedException);
	}

	private LatestRatesResponse createLatestRatesResponse(String baseCurrency) {
		LatestRatesResponse latestRatesResponse = new LatestRatesResponse();
		latestRatesResponse.setBase(baseCurrency);
		latestRatesResponse.setRates(new HashMap<String, BigDecimal>() {{
			put("USD", new BigDecimal("1.122813"));
			put("ARS", new BigDecimal("46.956363"));
			put("GBP", new BigDecimal("0.864768"));
			put("BRL", new BigDecimal("4.422794"));
			put("CLP", new BigDecimal("743.751855"));
		}});

		return latestRatesResponse;
	}
}
