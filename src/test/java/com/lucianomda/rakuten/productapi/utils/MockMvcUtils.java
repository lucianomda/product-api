package com.lucianomda.rakuten.productapi.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class MockMvcUtils {

	private MockMvcUtils() {}

	/**
	 * Returns properly configured {@link MappingJackson2HttpMessageConverter} matching the one from application properties (application.yml).
	 * NOTE: this must be up to date with application configuration or mock mvc tests will fail.
	 * @return A {@link MappingJackson2HttpMessageConverter} instance with same configuration than application properties.
	 */
	public static MappingJackson2HttpMessageConverter createJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.setDateFormat(new SimpleDateFormat(Constants.ISO_8601_DATE_FORMAT));
		objectMapper.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
		jackson2HttpMessageConverter.setObjectMapper(objectMapper);

		return jackson2HttpMessageConverter;
	}
}
