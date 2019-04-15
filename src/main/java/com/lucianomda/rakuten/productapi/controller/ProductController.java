package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.controller.model.ProductCreateRequest;
import com.lucianomda.rakuten.productapi.controller.model.ProductCreateResponse;
import com.lucianomda.rakuten.productapi.controller.model.ProductGetResponse;
import com.lucianomda.rakuten.productapi.service.ProductService;
import com.lucianomda.rakuten.productapi.service.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashSet;

@RestController
@Validated
public class ProductController {

	private ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping(path = "/products")
	public ResponseEntity<ProductCreateResponse> create(
			@RequestBody @Valid ProductCreateRequest product
	) {

		com.lucianomda.rakuten.productapi.service.model.Product productBean =
				com.lucianomda.rakuten.productapi.service.model.Product.builder()
						.name(product.getName())
						.categoryIds(new HashSet<>(product.getCategoryIds()))
						.price(product.getPrice())
						.currencyCode(product.getCurrencyCode())
						.build();

		productService.create(productBean);

		return ResponseEntity.status(HttpStatus.CREATED).body(ProductCreateResponse.builder().id(productBean.getId())
				.build());
	}

	@DeleteMapping(path = "products/{productId}")
	public ResponseEntity delete(@PathVariable(name = "productId") @Min(1) long id) {
		productService.deleteById(id);

		return ResponseEntity.ok(new Object());
	}

	@GetMapping(path = "products/{productId}")
	public ResponseEntity<ProductGetResponse> get(@PathVariable(name = "productId") @Min(1) long id) {
		Product product = productService.getById(id);

		return ResponseEntity.ok(ProductGetResponse.builder()
				.id(product.getId())
				.dateCreated(product.getDateCreated())
				.lastUpdated(product.getLastUpdated())
				.categories(product.getCategoryIds())
				.price(product.getPrice())
				.currencyCode(product.getCurrencyCode())
				.build());
	}
}
