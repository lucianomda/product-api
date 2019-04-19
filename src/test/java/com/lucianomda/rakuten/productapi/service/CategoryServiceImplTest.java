package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.persistence.repository.CategoryRepository;
import com.lucianomda.rakuten.productapi.persistence.repository.ProductRepository;
import com.lucianomda.rakuten.productapi.service.mapper.CategoryMapper;
import com.lucianomda.rakuten.productapi.service.model.Category;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoryServiceImplTest {

	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryMapper categoryMapper;
	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void create_success() {
		long expectedId = 129389;
		Category category = new Category();
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();

		when(categoryMapper.toDto(category)).thenReturn(categoryDto);
		when(categoryRepository.save(categoryDto)).thenAnswer(args -> {
			com.lucianomda.rakuten.productapi.persistence.model.Category c = args.getArgument(0);
			c.setId(expectedId);
			return c;
		});

		categoryService.create(category);
		verify(categoryMapper).toDto(category);
		verify(categoryRepository).save(categoryDto);
		assertThat(category.getId()).isEqualTo(expectedId);
	}

	@Test
	public void create_failureOnMapper() {
		Category category = new Category();
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();

		RuntimeException expectedException = new RuntimeException("test rte on mapper...");
		when(categoryMapper.toDto(category)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.create(category)).isEqualTo(expectedException);
		verify(categoryMapper).toDto(category);
		verify(categoryRepository, never())
				.save(any(com.lucianomda.rakuten.productapi.persistence.model.Category.class));

		assertThat(category.getId()).isNull();
	}

	@Test
	public void create_failureOnRepository() {
		Category category = new Category();
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();

		when(categoryMapper.toDto(category)).thenReturn(categoryDto);
		RuntimeException expectedException = new RuntimeException("test rte on repository...");
		when(categoryRepository.save(categoryDto)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.create(category)).isEqualTo(expectedException);
		verify(categoryMapper).toDto(category);
		verify(categoryRepository).save(categoryDto);
		assertThat(category.getId()).isNull();
	}

	@Test
	public void create_invalidParentId() {
		Category category = Category.builder().parentId(12L).build();
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setParent(null);

		when(categoryMapper.toDto(category)).thenReturn(categoryDto);

		assertThatThrownBy(() -> categoryService.create(category)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid parent Id 12.");

		verify(categoryMapper).toDto(category);
		verify(categoryRepository, never()).save(categoryDto);
	}

	@Test
	public void getById_success() {
		long id = 1983991;
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setId(id);
		Category category = Category.builder().id(id).build();

		when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryDto));
		when(categoryMapper.fromDto(categoryDto)).thenReturn(category);

		Category result = categoryService.getById(id);
		verify(categoryRepository).findById(id);
		verify(categoryMapper).fromDto(categoryDto);
		assertThat(result).isEqualTo(category);
	}

	@Test
	public void getById_invalidId() {
		long id = 1983991;
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setId(id);

		when(categoryRepository.findById(id)).thenReturn(Optional.empty());

		Category result = categoryService.getById(id);
		verify(categoryRepository).findById(id);
		verify(categoryMapper, never())
				.fromDto(any(com.lucianomda.rakuten.productapi.persistence.model.Category.class));

		assertThat(result).isNull();
	}

	@Test
	public void getById_failureOnRepository() {
		long id = 1983991;
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setId(id);

		RuntimeException expectedException = new RuntimeException("test error on repository findById...");
		when(categoryRepository.findById(id)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.getById(id)).isEqualTo(expectedException);
		verify(categoryRepository).findById(id);
		verify(categoryMapper, never())
				.fromDto(any(com.lucianomda.rakuten.productapi.persistence.model.Category.class));
	}

	@Test
	public void getById_failureOnMapper() {
		long id = 1983991;
		com.lucianomda.rakuten.productapi.persistence.model.Category categoryDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Category();
		categoryDto.setId(id);
		Category category = Category.builder().id(id).build();

		when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryDto));
		when(categoryMapper.fromDto(categoryDto)).thenReturn(category);

		Category result = categoryService.getById(id);
		verify(categoryRepository).findById(id);
		verify(categoryMapper).fromDto(categoryDto);
		assertThat(result).isEqualTo(category);
	}

	@Test
	public void deleteById_success() {
		long id = 87213;

		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenReturn(0L);
		when(productRepository.countByCategories_Id(id)).thenReturn(0L);

		categoryService.deleteById(id);
		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository).countByCategories_Id(id);
		verify(categoryRepository).deleteById(id);
	}

	@Test
	public void deleteById_invalidId() {
		long id = 87213;

		when(categoryRepository.existsById(id)).thenReturn(false);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid category id 87213.");

		verify(categoryRepository).existsById(id);
		verify(categoryRepository, never()).countByParentId(anyLong());
		verify(productRepository, never()).countByCategories_Id(anyLong());
		verify(categoryRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_failureWhileCheckingExistence() {
		long id = 87213;

		RuntimeException expectedException = new RuntimeException("test error while checking existence...");
		when(categoryRepository.existsById(id)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isEqualTo(expectedException);
		verify(categoryRepository).existsById(id);
		verify(categoryRepository, never()).countByParentId(anyLong());
		verify(productRepository, never()).countByCategories_Id(anyLong());
		verify(categoryRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_stillHasChildren() {
		long id = 87213;

		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenReturn(1L);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isInstanceOf(IllegalArgumentException.class).hasMessage("Category with id 87213 still has children.");
		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository, never()).countByCategories_Id(anyLong());
		verify(categoryRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_failureWhileCheckingChildrenCount() {
		long id = 87213;

		RuntimeException expectedException = new RuntimeException("test error while deleting...");
		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isEqualTo(expectedException);
		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository, never()).countByCategories_Id(anyLong());
		verify(categoryRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_stillHasProductsAssigned() {
		long id = 87213;

		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenReturn(0L);
		when(productRepository.countByCategories_Id(id)).thenReturn(1L);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Category 87213 can't be deleted because still have products assigned.");

		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository).countByCategories_Id(id);
		verify(categoryRepository, never()).deleteById(id);
	}

	@Test
	public void deleteById_failureWhileCheckingNoProductsAssigned() {
		long id = 87213;

		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenReturn(0L);
		RuntimeException expectedException = new RuntimeException("test error while deleting...");
		when(productRepository.countByCategories_Id(id)).thenThrow(expectedException);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isEqualTo(expectedException);
		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository).countByCategories_Id(anyLong());
		verify(categoryRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_failureWhileDeleting() {
		long id = 87213;

		RuntimeException expectedException = new RuntimeException("test error while deleting...");
		when(categoryRepository.existsById(id)).thenReturn(true);
		when(categoryRepository.countByParentId(id)).thenReturn(0L);
		when(productRepository.countByCategories_Id(id)).thenReturn(0L);
		doThrow(expectedException).when(categoryRepository).deleteById(id);

		assertThatThrownBy(() -> categoryService.deleteById(id)).isEqualTo(expectedException);
		verify(categoryRepository).existsById(id);
		verify(categoryRepository).countByParentId(id);
		verify(productRepository).countByCategories_Id(id);
		verify(categoryRepository).deleteById(id);
	}
}
