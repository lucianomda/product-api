package com.lucianomda.rakuten.productapi.service.mapper;

import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.service.model.Category;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoryMapperImplTest {

	@Mock
	private CategoryRepository categoryRepository;
	@InjectMocks
	private CategoryMapperImpl categoryMapper;
	@Captor
	private ArgumentCaptor<Iterable<Long>> categoryIdsCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void fromDto_withAllFields() {
		long[] expectedChildrenIds = new long[] {92839L, 1939000L, 9812838332L};
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setId(2132L);
		categoryDto.setDateCreated(new Date());
		categoryDto.setLastUpdated(new Date());
		categoryDto.setName("test category name...");
		categoryDto.setParent(new com.lucianomda.rakuten.productapi.persistence.model.Category());
		categoryDto.getParent().setId(3123L);
		categoryDto.setChildren(createCategoriesWithIds(expectedChildrenIds));

		Category result = categoryMapper.fromDto(categoryDto);
		assertThat(result.getId()).isEqualTo(categoryDto.getId());
		assertThat(result.getDateCreated()).isEqualTo(categoryDto.getDateCreated());
		assertThat(result.getLastUpdated()).isEqualTo(categoryDto.getLastUpdated());
		assertThat(result.getName()).isEqualTo(categoryDto.getName());
		assertThat(result.getParentId()).isEqualTo(categoryDto.getParent().getId());
		assertThat(result.getChildrenIds()).containsExactlyInAnyOrder(ArrayUtils.toObject(expectedChildrenIds));
	}

	private Set<com.lucianomda.rakuten.productapi.persistence.model.Category> createCategoriesWithIds(long... ids) {
		Set<com.lucianomda.rakuten.productapi.persistence.model.Category> result = new HashSet<>();
		for (long id : ids) {
			com.lucianomda.rakuten.productapi.persistence.model.Category c =
					new com.lucianomda.rakuten.productapi.persistence.model.Category();
			c.setId(id);
			result.add(c);
		}

		return result;
	}

	@Test
	public void fromDto_withNullFields() {
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();

		Category result = categoryMapper.fromDto(categoryDto);
		assertThat(result.getId()).isNull();
		assertThat(result.getDateCreated()).isNull();
		assertThat(result.getLastUpdated()).isNull();
		assertThat(result.getName()).isNull();
		assertThat(result.getParentId()).isNull();
		assertThat(result.getChildrenIds()).isNotNull().isEmpty();
	}

	@Test
	public void toDto_withAllFields() {
		Long[] expectedChildrenIds = new Long[] {92839L, 1939000L, 9812838332L};
		Category category = Category.builder()
				.id(19832L)
				.dateCreated(new Date())
				.lastUpdated(new Date())
				.name("test category 13...")
				.childrenIds(new HashSet<>(Arrays.asList(expectedChildrenIds)))
				.build();

		Set<com.lucianomda.rakuten.productapi.persistence.model.Category> categories =
				createCategoriesWithIds(ArrayUtils.toPrimitive(expectedChildrenIds));
		when(categoryRepository.findAllById(ArgumentMatchers.anyIterable())).thenReturn(categories);

		com.lucianomda.rakuten.productapi.persistence.model.Category result = categoryMapper.toDto(category);
		verify(categoryRepository).findAllById(categoryIdsCaptor.capture());
		assertThat(categoryIdsCaptor.getValue()).containsExactlyInAnyOrder(expectedChildrenIds);
		assertThat(result.getId()).isEqualTo(category.getId());
		assertThat(result.getDateCreated()).isEqualTo(category.getDateCreated());
		assertThat(result.getLastUpdated()).isEqualTo(category.getLastUpdated());
		assertThat(result.getName()).isEqualTo(category.getName());
		assertThat(result.getChildren()).isEqualTo(categories);
	}

	@Test
	public void toDto_withNullFields() {
		Category category = new Category();

		com.lucianomda.rakuten.productapi.persistence.model.Category result = categoryMapper.toDto(category);
		verify(categoryRepository, never()).findAllById(anyIterable());
		assertThat(result.getId()).isZero();
		assertThat(result.getDateCreated()).isNull();
		assertThat(result.getLastUpdated()).isNull();
		assertThat(result.getName()).isNull();
		assertThat(result.getChildren()).isNotNull().isEmpty();
	}
}
