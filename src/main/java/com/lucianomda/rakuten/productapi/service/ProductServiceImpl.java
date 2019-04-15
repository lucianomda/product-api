package com.lucianomda.rakuten.productapi.service;

import com.google.common.base.Preconditions;
import com.lucianomda.rakuten.productapi.persistence.repository.ProductRepository;
import com.lucianomda.rakuten.productapi.service.mapper.ProductMapper;
import com.lucianomda.rakuten.productapi.service.model.Product;
import com.lucianomda.rakuten.productapi.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Optional;

@Service
@Transactional
@Validated
public class ProductServiceImpl implements ProductService {

	private ProductRepository productRepository;
	private ProductMapper productMapper;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
	}

	public void create(@Valid Product product) {
		String currencyCode = product.getCurrencyCode();
		if (StringUtils.isBlank(currencyCode)) {
			currencyCode = Constants.DEFAULT_CURRENCY_CODE;
		} else {
			// TODO verify valid currency code
			if (!StringUtils.equals(currencyCode, Constants.DEFAULT_CURRENCY_CODE)) {
				throw new IllegalArgumentException("Invalid currency code '" + product.getCurrencyCode() + "'.");
			}
		}

		product.setCurrencyCode(currencyCode);

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto = productMapper.toDto(product);
		Preconditions.checkArgument(product.getCategoryIds().size() == productDto.getCategories().size(),
				"Some category ids are not valid %s.", product.getCategoryIds());

		productRepository.save(productDto);
		product.setId(productDto.getId());
	}

	public Product getById(@Min(1) long id) {
		Optional<com.lucianomda.rakuten.productapi.persistence.model.Product> optionalProductDto = productRepository.findById(id);
		if (optionalProductDto.isPresent()) {
			return productMapper.fromDto(optionalProductDto.get());
		}

		return null;
	}

	public void deleteById(@Min(1) long id) {
		Preconditions.checkArgument(productRepository.existsById(id), "Invalid product id.");
		productRepository.deleteById(id);
	}
}
