package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.service.model.Product;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface ProductService {

	/**
	 * Creates a product.
	 * @param product A @{link Product} instance.
	 * @throws IllegalArgumentException When {@link Product#categoryIds} contains any invalid category id (null and empty set are supported).
	 * @throws org.springframework.web.bind.MethodArgumentNotValidException When bean validation fails on {@link Product}.
	 */
	void create(@Valid Product product);

	/**
	 * Get a product by id.
	 * @param id A product id.
	 * @return A {@link Product} instance with specified id or null if none found.
	 * @throws IllegalArgumentException when id < 1.
	 * @throws javax.validation.ConstraintViolationException when id < 1.
	 */
	Product getById(@Min(1) long id);

	/**
	 * Deletes a product by id.
	 * @param id A product id.
	 *
	 * @throws IllegalArgumentException when no product exists with specified id.
	 * @throws javax.validation.ConstraintViolationException when id < 1.
	 */
	void deleteById(@Min(1) long id);
}
