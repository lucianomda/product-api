package com.lucianomda.rakuten.productapi.client.fixerio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fixerio")
@Setter
@Getter
public class FixerIoProperties {

	private String host;
	private String scheme;
	private String accessKey;
	private long cacheEvictionMinutes;
	private long cacheMaximumSize;
	private long httpConnectionTimeout;
	private long httpReadTimeout;
}
