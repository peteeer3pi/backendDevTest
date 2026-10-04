package com.itx.similarproducts.infrastructure.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  RestClient restClient(
      RestClient.Builder builder,
      @Value("${products.api.base-url}") String baseUrl,
      @Value("${products.api.connect-timeout}") Duration connectTimeout,
      @Value("${products.api.read-timeout}") Duration readTimeout) {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(readTimeout);

    return builder.baseUrl(baseUrl).requestFactory(requestFactory).build();
  }
}
