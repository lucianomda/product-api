package com.lucianomda.rakuten.productapi.controller.exception;

import com.lucianomda.rakuten.productapi.controller.model.ErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Exception used by spring mvc controllers to return an specific Http status code, error code an message.
 * @see com.lucianomda.rakuten.productapi.controller.handler.ApiErrorHandler
 */
@Getter @Setter
public class ApiErrorException extends RuntimeException {

	private final int status;
	private final ErrorCode errorCode;

	public ApiErrorException(int status, ErrorCode errorCode, String message) {
		this(status, errorCode, message, null);
	}

	public ApiErrorException(int status, ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
		this.errorCode = errorCode;
	}
}
