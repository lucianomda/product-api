package com.lucianomda.rakuten.productapi.service;

import com.google.common.base.Preconditions;
import com.lucianomda.rakuten.productapi.persistence.repository.ProductRepository;
import com.lucianomda.rakuten.productapi.service.model.Category;
import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.service.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.util.Optional;

@Service
@Transactional
@Validated
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	private CategoryRepository categoryRepository;
	private ProductRepository productRepository;
	private CategoryMapper categoryMapper;

	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository,
							   CategoryMapper categoryMapper) {

		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
		this.categoryMapper = categoryMapper;
	}

	@Override
	public void create(Category category) {
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto = categoryMapper.toDto(category);

		// Verifying if parent id exists in db.
		Preconditions.checkArgument(category.getParentId() == null
				|| (category.getParentId() != null && categoryDto.getParent() != null),
				"Invalid parent Id %s.", category.getParentId());

		categoryRepository.save(categoryDto);
		category.setId(categoryDto.getId());
		log.info("Category created: %s.", category);
	}

	@Override
	public Category getById(@Min(1) long id) {
		Optional<com.lucianomda.rakuten.productapi.persistence.model.Category> optionalCategoryDto =
				categoryRepository.findById(id);

		Category category = null;
		if (optionalCategoryDto.isPresent()) {
			category = categoryMapper.fromDto(optionalCategoryDto.get());
		}

		return category;
	}

	@Override
	public void deleteById(@Min(1) long id) {
		Preconditions.checkArgument(categoryRepository.existsById(id), "Invalid category id %s.", id);
		Preconditions.checkArgument(categoryRepository.countByParentId(id) == 0,
				"Category with id %s still has children.", id);
		Preconditions.checkArgument(productRepository.countByCategories_Id(id) == 0,
				"Category %s can't be deleted because still have products assigned.", id);

		categoryRepository.deleteById(id);
		log.info("Category %d deleted.", id);
	}
}
