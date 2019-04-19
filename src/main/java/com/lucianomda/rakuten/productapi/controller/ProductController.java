package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.controller.model.ProductCreateRequest;
import com.lucianomda.rakuten.productapi.controller.model.ProductCreateResponse;
import com.lucianomda.rakuten.productapi.controller.model.ProductGetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RequestMapping("/products")
public interface ProductController {

	/**
	 * Creates a product.
	 * @param productCreateRequest The request body is definded by {@link ProductCreateRequest} class.
	 * @return 201 for success.
	 */
	@PostMapping
	ResponseEntity<ProductCreateResponse> create(@RequestBody @Valid ProductCreateRequest productCreateRequest);

	/**
	 * Deletes a product.
	 * @param id A product id.
	 * @return 200 for success.
	 */
	@DeleteMapping(path = "/{productId}")
	ResponseEntity delete(@PathVariable(name = "productId") @Min(1) long id);

	/**
	 * Get a product by id.
	 * @param id A product id.
	 * @return 200 for success, the response body is defined by {@link ProductGetResponse} class.
	 */
	@GetMapping(path = "/{productId}")
	ResponseEntity<ProductGetResponse> get(@PathVariable(name = "productId") @Min(1) long id);
}
