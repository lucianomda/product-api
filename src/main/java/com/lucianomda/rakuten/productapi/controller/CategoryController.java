package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.controller.model.CategoryCreateRequest;
import com.lucianomda.rakuten.productapi.controller.model.CategoryCreateResponse;
import com.lucianomda.rakuten.productapi.controller.model.CategoryGetResponse;
import com.lucianomda.rakuten.productapi.service.CategoryService;
import com.lucianomda.rakuten.productapi.service.model.Category;
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

@RestController
@Validated
public class CategoryController {

	private CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@PostMapping(path = "/categories")
	public ResponseEntity<CategoryCreateResponse> create(
			@RequestBody @Valid CategoryCreateRequest categoryCreateRequest
	) {

		Category category =
				Category.builder()
						.name(categoryCreateRequest.getName())
						.parentId(categoryCreateRequest.getParentId())
						.build();

		categoryService.create(category);

		return ResponseEntity.status(HttpStatus.CREATED).body(CategoryCreateResponse.builder().id(category.getId())
				.build());
	}

	@DeleteMapping(path = "categories/{categoryId}")
	public ResponseEntity delete(@PathVariable(name = "categoryId") @Min(1) long id) {
		categoryService.deleteById(id);

		return ResponseEntity.ok(new Object());
	}

	@GetMapping(path = "categories/{categoryId}")
	public ResponseEntity<CategoryGetResponse> get(@PathVariable(name = "categoryId") @Min(1) long id) {
		Category category = categoryService.getById(id);

		return ResponseEntity.ok(CategoryGetResponse.builder()
				.id(category.getId())
				.dateCreated(category.getDateCreated())
				.lastUpdated(category.getLastUpdated())
				.name(category.getName())
				.childrenIds(category.getChildrenIds())
				.parentId(category.getParentId())
				.build());
	}
}
