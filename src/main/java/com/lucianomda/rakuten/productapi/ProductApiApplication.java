package com.lucianomda.rakuten.productapi;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.lucianomda.rakuten.productapi.client.fixerio.FixerIoProperties;
import com.lucianomda.rakuten.productapi.utils.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EntityScan("com.lucianomda.rakuten.productapi.persistence.model")
@EnableCaching
@CacheConfig
public class ProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
	}

	@Qualifier("fixerIoRestTemplate")
	@Bean(name = "fixerIoRestTemplate")
	public RestTemplate fixerIoRestTemplate(RestTemplateBuilder builder, FixerIoProperties fixerIoProperties) {
		return builder
				.requestFactory(HttpComponentsClientHttpRequestFactory.class)
				.setConnectTimeout(Duration.ofMillis(fixerIoProperties.getHttpConnectionTimeout()))
				.setReadTimeout(Duration.ofSeconds(fixerIoProperties.getHttpReadTimeout()))
				.build();
	}

	@Bean(name = "productApiCacheManager")
	public CacheManager productApiCacheManager(Cache fixerIoCache) {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Collections.singletonList(fixerIoCache));
		return cacheManager;
	}

	/**
	 * This caché will store fixer-io responses. TTL is configured on `application.yml`.
	 * This improves performance and make improvement of performance andas they are limited to
	 * @param fixerIoProperties A {@link FixerIoProperties} component filled with properties from spring boot configuration.
	 * @param caffeineCacheTicker A {@link Ticker}.
	 * @return A cache with TTL and max size configuration from spring boot properties configuration file.
	 */
	@Bean(name = "fixerIoCache")
	public Cache fixerIoCache(FixerIoProperties fixerIoProperties, Ticker caffeineCacheTicker) {
		return new CaffeineCache(Constants.FIXER_IO_CACHE,
				Caffeine.newBuilder()
						.ticker(caffeineCacheTicker)
						.expireAfterWrite(fixerIoProperties.getCacheEvictionMinutes(), TimeUnit.MINUTES)
						.maximumSize(fixerIoProperties.getCacheMaximumSize())
						.build());
	}

	/**
	 * A ticker shared by all caffeine cachés.
	 * @return A {@link Ticker} bean.
	 */
	@Bean(name = "caffeineCacheTicker")
	public Ticker caffeineCacheTicker() {
		return Ticker.systemTicker();
	}

}
