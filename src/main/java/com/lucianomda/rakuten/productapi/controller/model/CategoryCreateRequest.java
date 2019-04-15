package com.lucianomda.rakuten.productapi.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
public class CategoryCreateRequest {

	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	@Min(1)
	private Long parentId;
}
