package com.lucianomda.rakuten.productapi.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Object used on error responses for all RestControllers.
 * @see com.lucianomda.rakuten.productapi.controller.handler.ApiErrorHandler
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ApiError {

	private String errorCode;
	private String message;
}
