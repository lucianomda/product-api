package com.lucianomda.rakuten.productapi.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Product extends BaseEntity {

	@EqualsAndHashCode.Include
	@Column(nullable = false)
	private String name;
	@ManyToMany(cascade = {
			CascadeType.PERSIST, CascadeType.MERGE
	})
	private Set<Category> categories;
	@EqualsAndHashCode.Include
	@Column(nullable = false, precision = 11, scale = 2)
	private BigDecimal price;
	@EqualsAndHashCode.Include
	@Column(nullable = false, length = 3)
	private String currencyCode;
}
