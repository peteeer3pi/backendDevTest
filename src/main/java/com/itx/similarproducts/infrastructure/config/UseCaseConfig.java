package com.itx.similarproducts.infrastructure.config;

import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import com.itx.similarproducts.domain.usecase.FindSimilarProducts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  FindSimilarProducts findSimilarProducts(
      SimilarProductsFetcher similarProductsFetcher, ProductFetcher productFetcher) {
    return new FindSimilarProducts(similarProductsFetcher, productFetcher);
  }
}
