package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.service.model.Product;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapperImpl implements ProductMapper {

	private CategoryRepository categoryRepository;

	@Autowired
	public ProductMapperImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	/**
	 * Maps a product instance from service layer to a product from persistence layer.
	 * All category ids that do not exist are silently ignored and not mapped.
	 *
	 * @param product A product from service layer.
	 * @return A product instance from persistence layer.
	 */
	public com.lucianomda.rakuten.productapi.persistence.model.Product toDto(Product product) {
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setId(product.getId() != null ? product.getId() : 0L);
		productDto.setPrice(product.getPrice());
		productDto.setName(product.getName());
		productDto.setCurrencyCode(product.getCurrencyCode());
		productDto.setDateCreated(product.getDateCreated());
		productDto.setLastUpdated(product.getLastUpdated());

		Set<com.lucianomda.rakuten.productapi.persistence.model.Category> categories = new HashSet<>();
		if (CollectionUtils.isNotEmpty(product.getCategoryIds())) {
			categoryRepository.findAllById(product.getCategoryIds()).forEach(categories::add);
		}

		productDto.setCategories(categories);

		return productDto;
	}

	/**
	 * Maps a product instance from persistence layer to a product from service layer.
	 *
	 * @param productDto a product from persisten layer
	 * @return A product from service layer.
	 */
	public Product fromDto(com.lucianomda.rakuten.productapi.persistence.model.Product productDto) {
		Set<Long> categories = CollectionUtils.isEmpty(productDto.getCategories()) ?
				new HashSet<>()
				: productDto.getCategories().stream()
					.map(com.lucianomda.rakuten.productapi.persistence.model.Category::getId).collect(Collectors.toSet());

		return com.lucianomda.rakuten.productapi.service.model.Product
						.builder()
						.id(productDto.getId() > 0 ? productDto.getId() : null)
						.categoryIds(categories)
						.name(productDto.getName())
						.currencyCode(productDto.getCurrencyCode())
						.price(productDto.getPrice())
						.dateCreated(productDto.getDateCreated())
						.lastUpdated(productDto.getLastUpdated())
						.build();
	}
}
