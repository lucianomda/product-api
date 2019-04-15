package com.lucianomda.rakuten.productapi.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Category extends BaseEntity{

	@EqualsAndHashCode.Include
	@Column(nullable = false)
	private String name;
	@EqualsAndHashCode.Include
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	private Set<Category> children;
}
