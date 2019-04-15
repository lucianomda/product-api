package com.lucianomda.rakuten.productapi.controller.handler;

import com.lucianomda.rakuten.productapi.controller.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ApiErrorHandler {

	private static final String ERROR_CODE_BAD_REQUEST = "bad_request";

	/**
	 * Handles bean validation errors.
	 * @param e the {@link ConstraintViolationException} exception handled.
			* @return a bad request response.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> rulesForConstraintViolationException(ConstraintViolationException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ERROR_CODE_BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	/**
	 * Handles spring validation errors.
	 * @param e the {@link MethodArgumentNotValidException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> rulesForMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ERROR_CODE_BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	/**
	 * Handles custom validation errors.
	 * @param e the {@link IllegalArgumentException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> rulesForIllegalArgumentException(IllegalArgumentException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ERROR_CODE_BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	/**
	 * Handles all exceptions from RestControllers that are not catched specifically on this handler (see other methods).
	 *
	 * @return An {@link ApiError} instance.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> rulesForException(Exception e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode("internal_error");
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}
}
