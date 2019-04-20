package com.lucianomda.rakuten.productapi.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.lucianomda.rakuten.productapi.persistence.repository.ProductRepository;
import com.lucianomda.rakuten.productapi.service.mapper.ProductMapper;
import com.lucianomda.rakuten.productapi.service.model.Product;
import com.lucianomda.rakuten.productapi.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@Transactional
@Validated
@Slf4j
public class ProductServiceImpl implements ProductService {

	private static final ImmutableSet<String> supportedCurrencySet = new ImmutableSet.Builder<String>()
			.add(Constants.DEFAULT_CURRENCY_CODE)
			.build();

	private ProductRepository productRepository;
	private ProductMapper productMapper;
	private CurrencyExchangeService currencyExchangeService;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
							  CurrencyExchangeService currencyExchangeService) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
		this.currencyExchangeService = currencyExchangeService;
	}

	@Override
	public void create(@Valid Product product) {

		if (StringUtils.isBlank(product.getCurrencyCode())) {
			product.setCurrencyCode(Constants.DEFAULT_CURRENCY_CODE);
		}

		if (!supportedCurrencySet.contains(product.getCurrencyCode().toUpperCase())) {
			product.setPrice(currencyExchangeService.convertTo(product.getCurrencyCode(), product.getPrice(),
					Constants.DEFAULT_CURRENCY_CODE));
			product.setCurrencyCode(Constants.DEFAULT_CURRENCY_CODE);
		}

		product.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_UP));
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto = productMapper.toDto(product);
		Preconditions.checkArgument(product.getCategoryIds().size() == productDto.getCategories().size(),
				"Some category ids are not valid %s.", product.getCategoryIds());

		productRepository.save(productDto);
		product.setId(productDto.getId());
		log.info("Product created: {}.", product);
	}

	@Override
	public Product getById(@Min(1) long id) {
		Optional<com.lucianomda.rakuten.productapi.persistence.model.Product> optionalProductDto = productRepository.findById(id);
		if (optionalProductDto.isPresent()) {
			return productMapper.fromDto(optionalProductDto.get());
		}

		return null;
	}

	@Override
	public void deleteById(@Min(1) long id) {
		Preconditions.checkArgument(productRepository.existsById(id), "Invalid product id.");
		productRepository.deleteById(id);

		log.info("Product {} deleted", id);
	}
}
