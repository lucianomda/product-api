package com.lucianomda.rakuten.productapi.controller.handler;

import com.lucianomda.rakuten.productapi.controller.exception.ApiErrorException;
import com.lucianomda.rakuten.productapi.controller.model.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

public interface ApiErrorHandler {

	/**
	 * Handles spring constrain validation errors.
	 * @param e the {@link ConstraintViolationException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ApiError> rulesForConstraintViolationException(ConstraintViolationException e);

	/**
	 * Handles automatic bean validation errors on spring components using {@link javax.validation.Valid} annotation, eg. Controllers, Services, Repositories, etc.
	 *
	 * NOTE: This is a checked exception but as it is thrown by spring automatic checks, it is supposed to be handled by this Error handler only.
	 * Attempts to catch {@link MethodArgumentNotValidException} in any other place exception will result in a compilation error unless it is explicitly declared in method signature.
	 *
	 * @param e the {@link MethodArgumentNotValidException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> rulesForMethodArgumentNotValidException(MethodArgumentNotValidException e);

	/**
	 * Handles custom validation errors.
	 * @param e the {@link IllegalArgumentException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<ApiError> rulesForIllegalArgumentException(IllegalArgumentException e);

	/**
	 * Handles custom validation errors.
	 * @param e the {@link ApiErrorException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(ApiErrorException.class)
	ResponseEntity<ApiError> rulesForApiErrorException(ApiErrorException e);

	/**
	 * Handles all exceptions from RestControllers that are not catched specifically on this handler (see other methods).
	 *
	 * @return An {@link ApiError} instance.
	 */
	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> rulesForException(Exception e);
}
