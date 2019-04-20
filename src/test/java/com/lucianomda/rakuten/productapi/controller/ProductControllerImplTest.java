package com.lucianomda.rakuten.productapi.controller;


import com.lucianomda.rakuten.productapi.service.ProductService;
import com.lucianomda.rakuten.productapi.service.model.Product;
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class ProductControllerImplTest {

	@Captor
	private ArgumentCaptor<Product> productArgumentCaptor;
	@Mock
	private ProductService productService;
	@InjectMocks
	private ProductControllerImpl productController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(productController)
				.setMessageConverters(MockMvcUtils.createJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void create_success() throws Exception {
		long expectedId = 123;

		doAnswer(args -> {
			Product p = args.getArgument(0);
			p.setId(expectedId);

			return null;
		}).when(productService).create(any(Product.class));

		mockMvc.perform(post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"ginsu 2000\",\"category_ids\":[11,2,303],\"price\": \"123.12\",\"currency_code\":\"KPW\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(expectedId));

		verify(productService).create(productArgumentCaptor.capture());
		Product product = productArgumentCaptor.getValue();
		assertThat(product.getId()).isEqualTo(expectedId);
		assertThat(product.getPrice()).isEqualTo(new BigDecimal("123.12"));
		assertThat(product.getName()).isEqualTo("ginsu 2000");
		assertThat(product.getCategoryIds()).containsExactlyInAnyOrder(11L ,2L, 303L);
		assertThat(product.getCurrencyCode()).isEqualTo("KPW");
		assertThat(product.getDateCreated()).isNull();
		assertThat(product.getLastUpdated()).isNull();
	}

	@Test
	public void create_exception() {
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(productService).create(any(Product.class));

		assertThatThrownBy(() -> mockMvc.perform(post("/products")
				.contentType("application/json")
				.content("{\"name\":\"Fear of the Dark\",\"category_ids\":[1599],\"price\": \"9.99\",\"currency_code\":\"GBP\"}")))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);

		verify(productService).create(productArgumentCaptor.capture());
		Product product = productArgumentCaptor.getValue();
		assertThat(product.getId()).isNull();
		assertThat(product.getPrice()).isEqualTo(new BigDecimal("9.99"));
		assertThat(product.getName()).isEqualTo("Fear of the Dark");
		assertThat(product.getCategoryIds()).containsExactlyInAnyOrder(1599L);
		assertThat(product.getCurrencyCode()).isEqualTo("GBP");
		assertThat(product.getDateCreated()).isNull();
		assertThat(product.getLastUpdated()).isNull();
	}

	@Test
	public void delete_success() throws Exception {
		long productId = 123;

		mockMvc.perform(delete("/products/{productId}", productId))
				.andExpect(status().isOk())
				.andExpect(content().string(StringUtils.EMPTY));

		verify(productService).deleteById(productId);
	}

	@Test
	public void delete_exception()  {
		long productId = 123;
		RuntimeException expectedException = new RuntimeException();
		doThrow(expectedException).when(productService).deleteById(productId);

		assertThatThrownBy(() -> mockMvc.perform(delete("/products/{productId}", productId)))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);

		verify(productService).deleteById(productId);
	}

	@Test
	public void get_success() throws Exception {
		long productId = 123;
		DateFormat df = new SimpleDateFormat(Constants.ISO_8601_DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE));

		Product product = Product.builder()
				.id(productId)
				.lastUpdated(new Date())
				.dateCreated(new Date())
				.price(new BigDecimal("10.7"))
				.name("Black Heaven")
				.categoryIds(new HashSet<>(Arrays.asList(21L, 44L, 772727L, 555L)))
				.currencyCode("USD")
				.build();

		when(productService.getById(productId)).thenReturn(product);

		mockMvc.perform(get("/products/{productId}", productId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(productId))
				.andExpect(jsonPath("$.name").value(product.getName()))
				.andExpect(jsonPath("$.price").value(product.getPrice()))
				.andExpect(jsonPath("$.currency_code", equalTo(product.getCurrencyCode())))
				.andExpect(jsonPath("$.date_created", equalTo(df.format(product.getDateCreated()))))
				.andExpect(jsonPath("$.last_updated", equalTo(df.format(product.getLastUpdated()))))
				// As jackson json array of numbers uses Integer wrapper, we must use integers in matcher or assertion will fail.
				.andExpect(jsonPath("$.category_ids", containsInAnyOrder(21, 44, 772727, 555)));

		verify(productService).getById(productId);
	}

	@Test
	public void get_exception() {
		long productId = 123;
		RuntimeException expectedException = new RuntimeException();
		when(productService.getById(productId)).thenThrow(expectedException);

		assertThatThrownBy(() -> mockMvc.perform(get("/products/{productId}", productId)))
				.isInstanceOf(NestedServletException.class).hasCause(expectedException);

		verify(productService).getById(productId);
	}

	@Test
	public void get_invalidId() {
		long productId = 44123;

		assertThatThrownBy(() -> mockMvc.perform(get("/products/{productId}", productId)))
				.isInstanceOf(NestedServletException.class)
				.hasCauseInstanceOf(IllegalArgumentException.class)
				.hasFieldOrPropertyWithValue("cause.message", "Invalid product id 44123.");

		verify(productService).getById(productId);
	}
}
