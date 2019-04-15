package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.service.model.Product;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface ProductService {

	void create(@Valid Product product);
	Product getById(@Min(1) long id);
	void deleteById(@Min(1) long id);
}
