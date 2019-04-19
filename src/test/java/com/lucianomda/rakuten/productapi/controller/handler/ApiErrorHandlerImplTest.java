package com.lucianomda.rakuten.productapi.controller.handler;

import com.lucianomda.rakuten.productapi.controller.exception.ApiErrorException;
import com.lucianomda.rakuten.productapi.controller.model.ErrorCode;
import com.lucianomda.rakuten.productapi.service.model.Product;
import com.lucianomda.rakuten.productapi.utils.MockMvcUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiErrorHandlerImplTest {

	@Spy
	private TestController testController;
	@InjectMocks
	private ApiErrorHandlerImpl apiErrorHandler;

	private MockMvc mockMvc;

	@RestController
	@Validated
	public static class TestController {

		@DeleteMapping(path = "/test_iae")
		public ResponseEntity testIllegalArgumentException() {
			return null;
		}

		@PostMapping(path = "/test_manve")
		public ResponseEntity testMethodArgumentNotValidException(@RequestBody @Valid Product product) {
			return null;
		}

		@GetMapping(path = "/test_cve/{id}")
		public ResponseEntity testConstraintViolationException(@PathVariable(name = "id") @Min(10) long id) {
			return null;
		}
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(testController)
				.setMessageConverters(MockMvcUtils.createJackson2HttpMessageConverter())
				.setControllerAdvice(apiErrorHandler)
				.setValidator(new LocalValidatorFactoryBean())
				.build();
	}

	@Test
	public void rulesForMethodArgumentNotValidException() throws Exception {
		mockMvc.perform(post("/test_manve").content("{}").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error_code", equalTo(ErrorCode.BAD_REQUEST.toString())))
				.andExpect(jsonPath("$.message", startsWith("Validation failed for argument [0] in")));
	}

	@Test
	public void rulesFoConstraintViolationException() throws Exception {
		// Generating ConstraintViolation set running bean validation over an incomplete product instance.
		// Create ConstraintViolations manually is a nightmare.
		Product product = new Product();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Product>> constraintViolationSet = validator.validate(product);

		ConstraintViolationException cve = new ConstraintViolationException("Test ConstraintViolationException message.", constraintViolationSet);

		// Spring MockMvc standalone test does not supports Validated fields on MVC controllers, so it is firing ConstraintViolationException using mockito.
		when(testController.testConstraintViolationException(9)).thenThrow(cve);

		mockMvc.perform(get("/test_cve/9"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error_code", equalTo(ErrorCode.BAD_REQUEST.toString())))
				.andExpect(jsonPath("$.message", equalTo("Test ConstraintViolationException message.")));
	}

	@Test
	public void rulesForIllegalArgumentException() throws Exception {
		String expectedMessage = "test iae message.";
		when(testController.testIllegalArgumentException()).thenThrow(new IllegalArgumentException(expectedMessage));

		mockMvc.perform(delete("/test_iae"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error_code", equalTo(ErrorCode.BAD_REQUEST.toString())))
				.andExpect(jsonPath("$.message", equalTo(expectedMessage)));
	}

	@Test
	public void rulesForException() throws Exception {
		RuntimeException runtimeException = new RuntimeException("Test rte message.");
		when(testController.testIllegalArgumentException()).thenThrow(runtimeException);

		mockMvc.perform(delete("/test_iae"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error_code", equalTo(ErrorCode.INTERNAL_ERROR.toString())))
				.andExpect(jsonPath("$.message", equalTo(runtimeException.getMessage())));
	}

	@Test
	public void rulesForApiErrorException() throws Exception {
		ApiErrorException apiErrorException = new ApiErrorException(418, ErrorCode.INTERNAL_ERROR, "Teapot communication protocol error, can't prepare coffee.");
		when(testController.testIllegalArgumentException()).thenThrow(apiErrorException);

		mockMvc.perform(delete("/test_iae"))
				.andExpect(status().isIAmATeapot())
				.andExpect(jsonPath("$.error_code", equalTo(ErrorCode.INTERNAL_ERROR.toString())))
				.andExpect(jsonPath("$.message", equalTo(apiErrorException.getMessage())));
	}
}
