package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.service.model.Category;

public interface CategoryMapper {

	com.lucianomda.rakuten.productapi.persistence.model.Category toDto(Category category);
	Category fromDto(com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto);
}
