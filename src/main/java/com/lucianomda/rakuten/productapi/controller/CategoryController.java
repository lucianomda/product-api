package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.controller.model.CategoryCreateRequest;
import com.lucianomda.rakuten.productapi.controller.model.CategoryCreateResponse;
import com.lucianomda.rakuten.productapi.controller.model.CategoryGetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RequestMapping("/categories")
public interface CategoryController {

	/**
	 * Creates a Category.
	 * @param categoryCreateRequest The request body.
	 * @return 201 status for success.
	 * @see CategoryCreateResponse for body response format information.
	 */
	@PostMapping
	ResponseEntity<CategoryCreateResponse> create(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest);

	/**
	 * Deletes a category.
	 * @param id A category id.
	 * @return 200 for success.
	 */
	@DeleteMapping(path = "/{categoryId}")
	ResponseEntity delete(@PathVariable(name = "categoryId") @Min(1) long id);

	/**
	 * Get a category.
	 * @param id A category id.
	 * @return 200 for success. The body from response is defined by {@link CategoryGetResponse} class.
	 */
	@GetMapping(path = "/{categoryId}")
	ResponseEntity<CategoryGetResponse> get(@PathVariable(name = "categoryId") @Min(1) long id);
}
