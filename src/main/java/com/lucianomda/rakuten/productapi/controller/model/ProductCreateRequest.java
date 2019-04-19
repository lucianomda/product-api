package com.lucianomda.rakuten.productapi.controller.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class ProductCreateRequest {

	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	@NotNull
	private List<Long> categoryIds;
	@NotNull
	@DecimalMin("0.01")
	private BigDecimal price;
	@Size(min = 3, max = 3)
	private String currencyCode;
}
