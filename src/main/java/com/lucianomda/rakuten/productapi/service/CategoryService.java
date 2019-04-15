package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.service.model.Category;

import javax.validation.constraints.Min;

public interface CategoryService {

	void create(Category category);
	Category getById(@Min(1) long id);
	void deleteById(@Min(1) long id);
}
