package com.lucianomda.rakuten.productapi.controller.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
public class CategoryCreateRequest {

	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	@Min(1)
	private Long parentId;
}
