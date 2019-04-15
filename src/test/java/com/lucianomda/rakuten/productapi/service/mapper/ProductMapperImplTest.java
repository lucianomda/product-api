package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.persistence.model.Category;
import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.service.model.Product;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.when;

public class ProductMapperImplTest {

	@Mock
	private CategoryRepository categoryRepository;
	@InjectMocks
	private ProductMapperImpl productMapper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void toDto() {
		Product product = Product.builder()
				.categoryIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)))
				.currencyCode("EUR")
				.name("Ginsu 2000")
				.id(123432L)
				.price(BigDecimal.TEN)
				.build();

		Set<Category> categories = new HashSet<>();
		categories.add(new Category());
		categories.add(new Category());
		categories.add(new Category());
		when(categoryRepository.findAllById(anyIterable())).thenReturn(categories);

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto = productMapper.toDto(product);
		assertThat(productDto.getId()).isEqualTo(product.getId());
		assertThat(productDto.getName()).isEqualTo(product.getName());
		assertThat(productDto.getCurrencyCode()).isEqualTo(product.getCurrencyCode());
		assertThat(productDto.getPrice()).isEqualTo(product.getPrice());
		assertThat(productDto.getCategories()).containsAll(categories);
		assertThat(productDto.getCategories()).isNotSameAs(categories);
	}

	@Test
	public void toDto_withNullValues() {
		Product product = new Product();

		// CrudRepository returns empty iterator when no results are found.
		when(categoryRepository.findAllById(anyIterable())).thenReturn(new ArrayList<>());

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto = productMapper.toDto(product);
		assertThat(productDto.getId()).isEqualTo(0L);
		assertThat(productDto.getName()).isNull();
		assertThat(productDto.getCurrencyCode()).isNull();
		assertThat(productDto.getPrice()).isNull();
		assertThat(productDto.getCategories()).isNotNull().isEmpty();
	}

	@Test
	public void fromDto_withAllFields() {
		long[] expectedCategoryIds = new long[] {123L, 32L, 99L};
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setId(134L);
		productDto.setCurrencyCode("ARS");
		productDto.setName("AB Circle");
		productDto.setPrice(new BigDecimal("22.22"));
		productDto.setCategories(createCategoryListWithIds(expectedCategoryIds));

		Product result = productMapper.fromDto(productDto);
		assertThat(result.getId()).isEqualTo(productDto.getId());
		assertThat(result.getCurrencyCode()).isEqualTo(productDto.getCurrencyCode());
		assertThat(result.getName()).isEqualTo(productDto.getName());
		assertThat(result.getPrice()).isEqualTo(productDto.getPrice());
		assertThat(result.getCategoryIds()).containsExactlyInAnyOrder(ArrayUtils.toObject(expectedCategoryIds));
	}

	@Test
	public void fromDto_withNullFields() {
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		Product result = productMapper.fromDto(productDto);
		assertThat(result.getId()).isNull();
		assertThat(result.getCurrencyCode()).isNull();
		assertThat(result.getName()).isNull();
		assertThat(result.getPrice()).isNull();
		assertThat(result.getCategoryIds()).isNotNull().isEmpty();
	}

	@Test
	public void fromDto_withEmptyCategories() {
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();
		productDto.setCategories(new HashSet<>());

		Product result = productMapper.fromDto(productDto);
		assertThat(result.getCategoryIds()).isNotNull().isEmpty();
	}

	private Set<Category> createCategoryListWithIds(long... ids) {
		Set<Category> categories = new HashSet<>();
		for (long id : ids) {
			Category category = new Category();
			category.setId(id);
			categories.add(category);
		}

		return categories;
	}
}
