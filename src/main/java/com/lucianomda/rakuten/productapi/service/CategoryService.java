package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.service.model.Category;

import javax.validation.constraints.Min;

public interface CategoryService {

	/**
	 * Creates a category.
	 * @param category The object to be created.
	 * @throws IllegalArgumentException When {@link Category#parentId} is not valid (null is supported).
	 * @throws org.springframework.web.bind.MethodArgumentNotValidException When bean validation fails on {@link Category}.
	 */
	void create(Category category);

	/**
	 * Get a category by Id.
	 * @param id A category Id.
	 * @return A {@link Category} if found or null otherwise.
	 * @throws javax.validation.ConstraintViolationException when id < 1.
	 */
	Category getById(@Min(1) long id);

	/**
	 * Delete a category by Id.
	 * @param id A category Id.
	 *
	 * @throws javax.validation.ConstraintViolationException when id < 1.
	 * @throws IllegalArgumentException when no Category exists with specified id, if it has child categories or if it has products assigned.
	 */
	void deleteById(@Min(1) long id);
}
