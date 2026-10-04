package com.itx.similarproducts.infrastructure.config;

import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import com.itx.similarproducts.domain.usecase.FindSimilarProducts;
import java.util.concurrent.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public FindSimilarProducts findSimilarProducts(
      SimilarProductsFetcher similarProductsFetcher,
      ProductFetcher productFetcher,
      ExecutorService executorService) {

    return new FindSimilarProducts(similarProductsFetcher, productFetcher, executorService);
  }
}
