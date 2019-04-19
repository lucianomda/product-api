package com.lucianomda.rakuten.productapi.controller.handler;

import com.lucianomda.rakuten.productapi.controller.exception.ApiErrorException;
import com.lucianomda.rakuten.productapi.controller.model.ApiError;
import com.lucianomda.rakuten.productapi.controller.model.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ApiErrorHandlerImpl implements ApiErrorHandler {

	@Override
	public ResponseEntity<ApiError> rulesForConstraintViolationException(ConstraintViolationException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	@Override
	public ResponseEntity<ApiError> rulesForMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	@Override
	public ResponseEntity<ApiError> rulesForIllegalArgumentException(IllegalArgumentException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.BAD_REQUEST);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	@Override
	public ResponseEntity<ApiError> rulesForApiErrorException(ApiErrorException e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(e.getErrorCode());
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(apiError);
	}

	@Override
	public ResponseEntity<ApiError> rulesForException(Exception e) {
		log.error(e.getMessage(), e);

		ApiError apiError = new ApiError();
		apiError.setErrorCode(ErrorCode.INTERNAL_ERROR);
		apiError.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}
}
