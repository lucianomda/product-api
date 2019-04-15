package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.persistence.model.Product;

public interface ProductMapper {

	Product toDto(com.lucianomda.rakuten.productapi.service.model.Product product);
	com.lucianomda.rakuten.productapi.service.model.Product fromDto(Product product);
}
