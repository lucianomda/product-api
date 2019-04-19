package com.lucianomda.rakuten.productapi.service;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

public interface CurrencyExchangeService {

	/**
	 * Convert a price from one currency to another.
	 * @param fromCurrencyCode Original currency code in ISO 4217 format.
	 * @param fromAmount Original money amount.
	 * @param toCurrencyCode The currency to which the amount will be converted in ISO 4217 format.
	 * @return The amount in specified currency rounded with scale 2 (2 decimals digits) and {@link java.math.RoundingMode#HALF_UP}
	 * @see BigDecimal#scale
	 * @see java.math.RoundingMode#HALF_UP
	 */
	BigDecimal convertTo(@Size(min=3, max=3) String fromCurrencyCode,
						 BigDecimal fromAmount,
						 @Size(min=3, max=3)String toCurrencyCode);
}
