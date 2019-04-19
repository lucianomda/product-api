package com.lucianomda.rakuten.productapi.client.fixerio;

import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import com.lucianomda.rakuten.productapi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FixerIoClient {

	private FixerIoProperties fixerIoProperties;
	private RestTemplate fixerIoRestTemplate;

	@Autowired
	public FixerIoClient(RestTemplate fixerIoRestTemplate, FixerIoProperties fixerIoProperties) {
		this.fixerIoRestTemplate = fixerIoRestTemplate;
		this.fixerIoProperties = fixerIoProperties;
	}

	@Cacheable(cacheManager = "productApiCacheManager", cacheNames = {Constants.FIXER_IO_CACHE},
			sync = true, key="#p0.toUpperCase()")
	public LatestRatesResponse getCurrencyExchanges(String base) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(fixerIoProperties.getScheme())
				.host(fixerIoProperties.getHost())
				.path("/latest")
				.queryParam("access_key", fixerIoProperties.getAccessKey())
				.queryParam("base", base)
				.build()
				.encode();

		return fixerIoRestTemplate.getForObject(uriComponents.toUriString(), LatestRatesResponse.class);
	}
}
