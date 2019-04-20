package com.lucianomda.rakuten.productapi.controller;

import com.lucianomda.rakuten.productapi.service.CategoryService;
import com.lucianomda.rakuten.productapi.service.model.Category;
import com.lucianomda.rakuten.productapi.utils.Constants;
import com.lucianomda.rakuten.productapi.utils.MockMvcUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerImplTest {

	@Captor
	private ArgumentCaptor<Category> categoryArgumentCaptor;
	@Mock
	private CategoryService categoryService;
	@InjectMocks
	private CategoryControllerImpl categoryController;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
				.setMessageConverters(MockMvcUtils.createJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void create_success() throws Exception {
		long expectedId = 123;

		doAnswer(args -> {
			Category p = args.getArgument(0);
			p.setId(expectedId);

			return null;
		}).when(categoryService).create(any(Category.class));

		mockMvc.perform(post("/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Knifes\",\"parent_id\":3033}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(expectedId));

		verify(categoryService).create(categoryArgumentCaptor.capture());
		Category product = categoryArgumentCaptor.getValue();
		assertThat(product.getId()).isEqualTo(expectedId);
		assertThat(product.getName()).isEqualTo("Knifes");
		assertThat(product.getParentId()).isEqualTo(3033L);
		assertThat(product.getChildrenIds()).isNull();
		assertThat(product.getDateCreated()).isNull();
		assertThat(product.getLastUpdated()).isNull();
	}

	@Test
	public void create_exception() {
		long expectedId = 123;
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(categoryService).create(any(Category.class));

		assertThatThrownBy(() -> mockMvc.perform(post("/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Knifes\",\"parent_id\":3033}")))
				.isInstanceOf(NestedServletException.class)
				.hasCause(expectedException);

		verify(categoryService).create(categoryArgumentCaptor.capture());
		Category product = categoryArgumentCaptor.getValue();
		assertThat(product.getId()).isNull();
		assertThat(product.getName()).isEqualTo("Knifes");
		assertThat(product.getParentId()).isEqualTo(3033L);
		assertThat(product.getChildrenIds()).isNull();
		assertThat(product.getDateCreated()).isNull();
		assertThat(product.getLastUpdated()).isNull();
	}

	@Test
	public void delete_success() throws Exception {
		long categoryId = 123;

		mockMvc.perform(delete("/categories/{categoryId}", categoryId))
				.andExpect(status().isOk())
				.andExpect(content().string(StringUtils.EMPTY));

		verify(categoryService).deleteById(categoryId);
	}

	@Test
	public void delete_exception() {
		long expectedId = 123;
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(categoryService).deleteById(expectedId);

		assertThatThrownBy(() -> mockMvc.perform(delete("/categories/{categoryId}", expectedId)))
				.isInstanceOf(NestedServletException.class)
				.hasCause(expectedException);

		verify(categoryService).deleteById(expectedId);
	}

	@Test
	public void get_success() throws Exception {
		long categoryId = 123;
		DateFormat df = new SimpleDateFormat(Constants.ISO_8601_DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));
		Category category = Category.builder()
				.id(categoryId)
				.name("Music albums")
				.parentId(12L)
				.lastUpdated(new Date())
				.dateCreated(new Date())
				.childrenIds(new HashSet<>(Arrays.asList(19L, 414L)))
				.build();
		when(categoryService.getById(categoryId)).thenReturn(category);

		mockMvc.perform(get("/categories/{categoryId}", categoryId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(categoryId))
				.andExpect(jsonPath("$.name").value(category.getName()))
				.andExpect(jsonPath("$.date_created", equalTo(df.format(category.getDateCreated()))))
				.andExpect(jsonPath("$.last_updated", equalTo(df.format(category.getLastUpdated()))))
				// As jackson json array of numbers uses Integer wrapper, we must use integers in matcher or assertion will fail.
				.andExpect(jsonPath("$.parent_id", equalTo(category.getParentId().intValue())))
				.andExpect(jsonPath("$.children_ids", containsInAnyOrder(19, 414)));

		verify(categoryService).getById(categoryId);
	}

	@Test
	public void get_exception() {
		long productId = 123;
		RuntimeException expectedException = new RuntimeException();
		when(categoryService.getById(productId)).thenThrow(expectedException);

		assertThatThrownBy(() -> mockMvc.perform(get("/categories/{productId}", productId)))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);

		verify(categoryService).getById(productId);
	}

	@Test
	public void get_invalidId() {
		long productId = 44123;

		assertThatThrownBy(() -> mockMvc.perform(get("/categories/{productId}", productId)))
				.isInstanceOf(NestedServletException.class)
				.hasCauseInstanceOf(IllegalArgumentException.class)
				.hasFieldOrPropertyWithValue("cause.message", "Invalid category id 44123.");

		verify(categoryService).getById(productId);
	}
}
