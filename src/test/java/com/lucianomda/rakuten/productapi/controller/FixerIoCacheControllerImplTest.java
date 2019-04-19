package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import com.lucianomda.rakuten.productapi.utils.Constants;
import com.lucianomda.rakuten.productapi.utils.MockMvcUtils;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(JUnitParamsRunner.class)
public class FixerIoCacheControllerImplTest {

	@Mock
	private Cache fixerIoCache;
	@InjectMocks
	private FixerIoCacheControllerImpl fixerIoCacheController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(fixerIoCacheController)
				.setMessageConverters(MockMvcUtils.createJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void evictAll_success() throws Exception {
		mockMvc.perform(post("/fixerio-cache/evict-all"))
				.andExpect(status().isOk())
				.andExpect(content().string(StringUtils.EMPTY));

		verify(fixerIoCache).clear();
	}

	@Test
	public void evictAll_exception() {
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(fixerIoCache).clear();

		assertThatThrownBy(() -> mockMvc.perform(post("/fixerio-cache/evict-all"))
				.andExpect(status().isOk())
				.andExpect(content().string(StringUtils.EMPTY))).isInstanceOf(NestedServletException.class).hasCause(expectedException);
		verify(fixerIoCache).clear();
	}

	@Test
	@Parameters(value = {"EUr", "ars", "USD"})
	public void evictValue_success(String currencyCode) throws Exception {
		mockMvc.perform(delete("/fixerio-cache/{currencyCode}", currencyCode))
				.andExpect(status().isOk())
				.andExpect(content().string(StringUtils.EMPTY));

		verify(fixerIoCache).evict(currencyCode.toUpperCase());
	}

	@Test
	public void evictValue_exception() {
		String currencyCode = "AOA";
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(fixerIoCache).evict(currencyCode);

		assertThatThrownBy(() -> mockMvc.perform(delete("/fixerio-cache/{currencyCode}", currencyCode)))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);
		verify(fixerIoCache).evict(currencyCode);
	}

	@Test
	public void getValue_success() throws Exception {
		String currencyCode = "DZD";
		BigDecimal arsExchangeRate = new BigDecimal("0.123123");
		LatestRatesResponse latestRatesResponse = new LatestRatesResponse();
		latestRatesResponse.setBase(currencyCode);
		latestRatesResponse.setRates(new HashMap<>());
		latestRatesResponse.getRates().put("ARS", arsExchangeRate);
		when(fixerIoCache.get(currencyCode, LatestRatesResponse.class)).thenReturn(latestRatesResponse);

		mockMvc.perform(get("/fixerio-cache/{currencyCode}", currencyCode))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.base").value(currencyCode))
				.andExpect(jsonPath("$.rates.ARS").value(arsExchangeRate));

		verify(fixerIoCache).get(currencyCode, LatestRatesResponse.class);
	}

	@Test
	public void getValue_notFound() throws Exception {
		String currencyCode = "DZD";

		mockMvc.perform(get("/fixerio-cache/{currencyCode}", currencyCode))
				.andExpect(status().isNotFound())
				.andExpect(content().string(StringUtils.EMPTY));

		verify(fixerIoCache).get(currencyCode, LatestRatesResponse.class);
	}

	@Test
	public void getValue_exception() {
		String currencyCode = "DZD";
		RuntimeException expectedException = new RuntimeException();
		when(fixerIoCache.get(currencyCode, LatestRatesResponse.class)).thenThrow(expectedException);

		assertThatThrownBy(() -> mockMvc.perform(get("/fixerio-cache/{currencyCode}", currencyCode)))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);

		verify(fixerIoCache).get(currencyCode, LatestRatesResponse.class);
	}
}
