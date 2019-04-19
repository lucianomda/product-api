package com.lucianomda.rakuten.productapi.service;

import com.lucianomda.rakuten.productapi.persistence.repository.ProductRepository;
import com.lucianomda.rakuten.productapi.service.mapper.ProductMapper;
import com.lucianomda.rakuten.productapi.service.model.Product;
import com.lucianomda.rakuten.productapi.utils.Constants;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ProductServiceImplTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ProductMapper productMapper;
	@Mock
	private CurrencyExchangeService currencyExchangeService;
	@InjectMocks
	private ProductServiceImpl productService;

	@Before()
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void create_successWithNullCurrency() {
		long expectedId = 29318;
		Product product = Product.builder().price(BigDecimal.ONE).categoryIds(new HashSet<>()).build();
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setCategories(new HashSet<>());

		when(productMapper.toDto(product)).thenReturn(productDto);
		when(productRepository.save(productDto)).thenAnswer(args -> {
			com.lucianomda.rakuten.productapi.persistence.model.Product p = args.getArgument(0);
			p.setId(expectedId);

			return p;
		});

		productService.create(product);
		verify(productMapper).toDto(product);
		verify(productRepository).save(productDto);
		verify(currencyExchangeService, never()).convertTo(anyString(), any(BigDecimal.class), anyString());

		assertThat(product.getId()).isEqualTo(expectedId);
		assertThat(product.getCurrencyCode()).isEqualTo("EUR");
	}

	@Test
	public void create_successWithCurrencyEur() {
		long expectedId = 29318;
		String currencyCode = "EUR";
		Product product = Product.builder().categoryIds(new HashSet<>()).currencyCode(currencyCode)
				.price(BigDecimal.ONE).build();

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setCategories(new HashSet<>());

		when(productMapper.toDto(product)).thenReturn(productDto);
		when(productRepository.save(productDto)).thenAnswer(args -> {
			com.lucianomda.rakuten.productapi.persistence.model.Product p = args.getArgument(0);
			p.setId(expectedId);

			return p;
		});

		productService.create(product);
		verify(productMapper).toDto(product);
		verify(productRepository).save(productDto);
		verify(currencyExchangeService, never()).convertTo(anyString(), any(BigDecimal.class), anyString());
		assertThat(product.getId()).isEqualTo(expectedId);
		assertThat(product.getCurrencyCode()).isEqualTo(currencyCode);
	}

	public static Object[][] productsAndPricesData() {
		return new Object[][] {
				new Object[] {
						Product.builder().price(new BigDecimal("18.835")).categoryIds(new HashSet<>()).currencyCode("EUR").build(),
						new BigDecimal("18.84")
				},
				new Object[] {
						Product.builder().price(new BigDecimal("9.99992221")).categoryIds(new HashSet<>()).currencyCode("EUR").build(),
						new BigDecimal("10.00")
				},
				new Object[] {
						Product.builder().price(new BigDecimal("12909.39299123")).categoryIds(new HashSet<>()).currencyCode(null).build(),
						new BigDecimal("12909.39")
				}
		};
	}

	@Test
	@Parameters(method = "productsAndPricesData")
	public void create_successRoundPriceTo2Digits(Product product, BigDecimal expectedPrice) {
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setCategories(new HashSet<>());

		when(productMapper.toDto(product)).thenReturn(productDto);

		productService.create(product);
		verify(productMapper).toDto(product);
		verify(productRepository).save(productDto);
		verify(currencyExchangeService, never()).convertTo(anyString(), any(BigDecimal.class), anyString());

		assertThat(product.getCurrencyCode()).isEqualTo("EUR");
		assertThat(product.getPrice()).isEqualTo(expectedPrice);
	}

	@Test
	@Parameters(value = {"ARS", "BRL", "GBP", "USD"})
	public void create_successWithOtherCurrency(String currencyCode) {
		long expectedId = 29318;
		BigDecimal price = new BigDecimal("10.02");
		BigDecimal expectedPrice = new BigDecimal("2.01");
		Product product = new Product();
		product.setCategoryIds(new HashSet<>());
		product.setCurrencyCode(currencyCode);
		product.setPrice(price);

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		productDto.setCategories(new HashSet<>());

		when(currencyExchangeService.convertTo(currencyCode, price, Constants.DEFAULT_CURRENCY_CODE)).thenReturn(expectedPrice);
		when(productMapper.toDto(product)).thenReturn(productDto);
		when(productRepository.save(productDto)).thenAnswer(args -> {
			com.lucianomda.rakuten.productapi.persistence.model.Product p = args.getArgument(0);
			p.setId(expectedId);

			return p;
		});

		productService.create(product);
		verify(productMapper).toDto(product);
		verify(productRepository).save(productDto);
		verify(currencyExchangeService).convertTo(currencyCode, price, Constants.DEFAULT_CURRENCY_CODE);
		assertThat(product.getId()).isEqualTo(expectedId);
		assertThat(product.getCurrencyCode()).isEqualTo(currencyCode);
		assertThat(product.getPrice()).isEqualTo(expectedPrice);
	}

	@Test
	public void create_failureOnMapper() {
		Product product = Product.builder().price(BigDecimal.ONE).build();

		RuntimeException expectedException = new RuntimeException("test error on mapper message");
		when(productMapper.toDto(product)).thenThrow(expectedException);

		assertThatThrownBy(() -> productService.create(product)).isEqualTo(expectedException);
		verify(productMapper).toDto(product);
		verify(productRepository, never()).save(any(com.lucianomda.rakuten.productapi.persistence.model.Product.class));
		assertThat(product.getId()).isNull();
	}

	@Test
	public void create_invalidCategoryIds() {
		Product product = Product.builder().price(BigDecimal.ONE)
				.categoryIds(new HashSet<>(Arrays.asList(1L, 2L))).build();

		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();
		productDto.setCategories(new HashSet<>());

		when(productMapper.toDto(product)).thenReturn(productDto);

		assertThatThrownBy(() -> productService.create(product)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Some category ids are not valid [1, 2].");
		verify(productMapper).toDto(product);
		verify(productRepository, never()).save(productDto);
		assertThat(product.getId()).isNull();
	}

	@Test
	public void create_failureOnRepository() {
		Product product = Product.builder().price(BigDecimal.ONE).build();
		product.setCategoryIds(new HashSet<>());
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();
		productDto.setCategories(new HashSet<>());

		when(productMapper.toDto(product)).thenReturn(productDto);
		RuntimeException expectedException = new RuntimeException("test error on repository message");
		when(productRepository.save(productDto)).thenThrow(expectedException);

		assertThatThrownBy(() -> productService.create(product)).isEqualTo(expectedException);
		verify(productMapper).toDto(product);
		verify(productRepository).save(productDto);
		assertThat(product.getId()).isNull();
	}

	@Test
	public void deleteById_success() {
		long productId = 123;

		when(productRepository.existsById(productId)).thenReturn(true);

		productService.deleteById(productId);
		verify(productRepository).existsById(productId);
		verify(productRepository).deleteById(productId);
	}

	@Test
	public void deleteById_failureBecauseIdDoesNotExists() {
		long productId = 123;

		when(productRepository.existsById(productId)).thenReturn(false);

		assertThatThrownBy(() -> productService.deleteById(productId))
				.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid product id.");

		verify(productRepository).existsById(productId);
		verify(productRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_failureByErrorWhenCheckingExistence() {
		long productId = 123;

		RuntimeException expectedException = new RuntimeException("test error while checking existence");
		when(productRepository.existsById(productId)).thenThrow(expectedException);

		assertThatThrownBy(() -> productService.deleteById(productId)).isEqualTo(expectedException);
		verify(productRepository).existsById(productId);
		verify(productRepository, never()).deleteById(anyLong());
	}

	@Test
	public void deleteById_failureDuringDeletion() {
		long productId = 123;

		when(productRepository.existsById(productId)).thenReturn(true);
		RuntimeException expectedException = new RuntimeException("test error while deleting.");
		doThrow(expectedException).when(productRepository).deleteById(productId);

		assertThatThrownBy(() -> productService.deleteById(productId)).isEqualTo(expectedException);
		verify(productRepository).existsById(productId);
		verify(productRepository).deleteById(productId);
	}

	@Test
	public void getById_success() {
		long productId = 882;
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();
		Product product = new Product();

		when(productRepository.findById(productId)).thenReturn(Optional.of(productDto));
		when(productMapper.fromDto(productDto)).thenReturn(product);

		Product result = productService.getById(productId);
		verify(productRepository).findById(productId);
		verify(productMapper).fromDto(productDto);
		assertThat(result).isEqualTo(product);
	}

	@Test
	public void getById_invalidId() {
		long productId = 667;
		com.lucianomda.rakuten.productapi.persistence.model.Product productDto =
				new com.lucianomda.rakuten.productapi.persistence.model.Product();

		when(productRepository.findById(productId)).thenReturn(Optional.empty());

		Product result = productService.getById(productId);
		verify(productRepository).findById(productId);
		verify(productMapper, never()).fromDto(any(com.lucianomda.rakuten.productapi.persistence.model.Product.class));
		assertThat(result).isNull();
	}
}
