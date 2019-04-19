package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import com.lucianomda.rakuten.productapi.controller.model.GetFixerIoCacheValueResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;

@Slf4j
@RestController
@Validated
public class FixerIoCacheControllerImpl implements FixerIoCacheController {

	private Cache fixerIoCache;

	@Autowired
	public FixerIoCacheControllerImpl(Cache fixerIoCache) {
		this.fixerIoCache = fixerIoCache;
	}

	@Override
	public ResponseEntity evictAll() {
		fixerIoCache.clear();
		log.info("All values from fixerIoCache were evicted successfully.");
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity evictValue(@PathVariable("currencyCode") @NotNull @Size(min = 3, max = 3) String currencyCode) {
		fixerIoCache.evict(currencyCode.toUpperCase());
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<GetFixerIoCacheValueResponse> getValue(
			@PathVariable("currencyCode") @NotNull @Size(min = 3, max = 3) String currencyCode) {
		LatestRatesResponse latestRatesResponse = fixerIoCache.get(currencyCode.toUpperCase(), LatestRatesResponse.class);
		if (latestRatesResponse == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}


		return ResponseEntity.ok(GetFixerIoCacheValueResponse.builder()
				.base(latestRatesResponse.getBase())
				.rates(new HashMap<>(latestRatesResponse.getRates()))
				.build());
	}
}
