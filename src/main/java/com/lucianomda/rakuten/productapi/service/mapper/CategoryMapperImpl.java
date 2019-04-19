package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.service.model.Category;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryMapperImpl implements CategoryMapper {

	@Autowired
	private CategoryRepository categoryRepository;

	public CategoryMapperImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public com.lucianomda.rakuten.productapi.persistence.model.Category toDto(Category category) {
		com.lucianomda.rakuten.productapi.persistence.model.Category parent = null;
		if (category.getParentId() != null) {
			parent = categoryRepository.findById(category.getParentId()).orElse(null);
		}

		Set<com.lucianomda.rakuten.productapi.persistence.model.Category> children = new HashSet<>();
		if (CollectionUtils.isNotEmpty(category.getChildrenIds())) {
			categoryRepository.findAllById(category.getChildrenIds()).forEach(children::add);
		}

		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();

		categoryDto.setId(category.getId() != null ? category.getId() : 0);
		categoryDto.setDateCreated(category.getDateCreated());
		categoryDto.setLastUpdated(category.getLastUpdated());
		categoryDto.setName(category.getName());
		categoryDto.setParent(parent);
		categoryDto.setChildren(children);

		return categoryDto;
	}

	public Category fromDto(com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto) {
		Set<Long> childrenIds = categoryDto.getChildren() != null ?
				categoryDto.getChildren()
						.stream().map(com.lucianomda.rakuten.productapi.persistence.model.Category::getId)
						.collect(Collectors.toSet())
				: new HashSet<>();

		return Category.builder()
				.id(categoryDto.getId() > 0L ? categoryDto.getId() : null)
				.dateCreated(categoryDto.getDateCreated())
				.lastUpdated(categoryDto.getLastUpdated())
				.name(categoryDto.getName())
				.parentId(categoryDto.getParent() != null ? categoryDto.getParent().getId() : null)
				.childrenIds(childrenIds)
				.build();
	}
}
