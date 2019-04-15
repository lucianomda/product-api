package com.lucianomda.rakuten.productapi.service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
public class Product {

	private Long id;
	private Date dateCreated;
	private Date lastUpdated;
	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	@NotNull
	private Set<Long> categoryIds;
	@NotNull
	@DecimalMin("0.01")
	private BigDecimal price;
	@Size(min = 3, max = 3)
	private String currencyCode;
}
