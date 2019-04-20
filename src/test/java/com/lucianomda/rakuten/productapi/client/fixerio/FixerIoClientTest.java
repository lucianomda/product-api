package com.lucianomda.rakuten.productapi.client.fixerio;

import com.google.common.io.Resources;
import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import com.lucianomda.rakuten.productapi.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpResponse.response;

public class FixerIoClientTest {

	private static RestTemplate restTemplate = new RestTemplate();
	private FixerIoProperties fixerIoProperties;
	private ClientAndServer mockServer;
	private FixerIoClient fixerIoClient;

	@Before
	public void startServer() throws IOException {
		fixerIoProperties = new FixerIoProperties();
		fixerIoProperties.setAccessKey("test_key");
		fixerIoProperties.setHost("127.0.0.1");
		fixerIoProperties.setPort(29996);
		fixerIoProperties.setScheme("http");
		fixerIoProperties.setHttpConnectionTimeout(100);
		fixerIoProperties.setHttpConnectionTimeout(100);
		fixerIoProperties.setCacheEvictionMinutes(1);
		fixerIoProperties.setCacheMaximumSize(10);

		mockServer = ClientAndServer.startClientAndServer(fixerIoProperties.getPort());
		mockFixerIoServerResponse(Constants.DEFAULT_CURRENCY_CODE);

		fixerIoClient = new FixerIoClient(restTemplate, fixerIoProperties);
	}

	@After
	public void stopServer(){
		mockServer.stop();
	}

	private void mockFixerIoServerResponse(String baseCurrencyCode) throws IOException {
		URL responseBodyUrl = this.getClass().getResource("/mock-server/fixer-io/latest-exchange-response.json");

		new MockServerClient("127.0.0.1", fixerIoProperties.getPort())
				.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/latest")
								.withQueryStringParameter("access_key", fixerIoProperties.getAccessKey())
								.withQueryStringParameter("base", baseCurrencyCode)
								.withHeader("\"Content-type\", \"application/json\"")
				)
				.respond(
						response()
								.withStatusCode(200)
								.withHeaders(
										new Header("Content-Type", "application/json; charset=utf-8"))
								.withBody(Resources.toString(responseBodyUrl, Charset.forName("UTF-8")))
				);
	}

	@Test
	public void getCurrencyExchanges() {
		LatestRatesResponse latestRatesResponse = fixerIoClient.getCurrencyExchanges("EUR");
		assertThat(latestRatesResponse.getBase()).isEqualTo("EUR");
		assertThat(latestRatesResponse.getRates().size()).isGreaterThan(1);
	}
}
