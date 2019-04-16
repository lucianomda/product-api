package com.lucianomda.rakuten.productapi.controller.handler;

import com.lucianomda.rakuten.productapi.controller.exception.ApiErrorException;
import com.lucianomda.rakuten.productapi.controller.model.ApiError;
import com.lucianomda.rakuten.productapi.controller.model.ErrorCode;
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

	/**
	 * Handles spring constrain validation errors.
	 * @param e the {@link ConstraintViolationException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> rulesForConstraintViolationException(ConstraintViolationException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

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
	public ResponseEntity<ApiError> rulesForMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
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
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	/**
	 * Handles custom validation errors.
	 * @param e the {@link ApiErrorException} exception handled.
	 * @return a bad request response.
	 */
	@ExceptionHandler(ApiErrorException.class)
	public ResponseEntity<ApiError> rulesForApiErrorException(ApiErrorException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(e.getErrorCode());
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(e.getStatus()).body(apiError);
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
		apiError.setErrorCode(ErrorCode.INTERNAL_ERROR);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}
}
