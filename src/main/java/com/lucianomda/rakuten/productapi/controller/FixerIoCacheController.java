package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.client.fixerio.vo.LatestRatesResponse;
import com.lucianomda.rakuten.productapi.controller.model.GetFixerIoCacheValueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RequestMapping("/fixerio-cache")
public interface FixerIoCacheController {

	/**
	 * Evicts all values from fixerio cache.
	 * @return status 200 for success and Body of response is empty JSON object.
	 */
	@PostMapping("/evict-all")
	ResponseEntity evictAll();

	/**
	 * Evicts one value from fixerio cache.
	 * @param currencyCode A currency code in ISO 4217 format (3 chars).
	 * @return status 200 for success, even if value was not contained in cache and body of response is empty JSON object.
	 */
	@DeleteMapping("/{currencyCode}")
	ResponseEntity evictValue(@PathVariable("currencyCode") @NotNull @Size(min = 3, max = 3) String currencyCode);

	/**
	 * Returns a value from fixerio cache.
	 * @param currencyCode A currency code in ISO 4217 format (3 chars).
	 * @return 200 if found or 404 if not. The body format is defined by {@link LatestRatesResponse} class.
	 */
	@GetMapping("/{currencyCode}")
	ResponseEntity<GetFixerIoCacheValueResponse> getValue(
			@PathVariable("currencyCode") @NotNull @Size(min = 3, max = 3) String currencyCode);
}
