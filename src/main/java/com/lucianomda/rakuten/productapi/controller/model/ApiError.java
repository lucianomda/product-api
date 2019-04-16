package com.lucianomda.rakuten.productapi.controller.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Object used on error responses for all RestControllers.
 * @see com.lucianomda.rakuten.productapi.controller.handler.ApiErrorHandler
 */
@Getter
@Setter
public class ApiError {

	private ErrorCode errorCode;
	private String message;
}
