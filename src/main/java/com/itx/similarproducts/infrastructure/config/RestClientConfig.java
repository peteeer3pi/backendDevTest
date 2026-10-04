package com.itx.similarproducts.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  RestClient restClient(
      RestClient.Builder builder, @Value("${products.api.base-url}") String baseUrl) {
    return builder.baseUrl(baseUrl).build();
  }
}
