package com.itx.similarproducts.infrastructure.service;

import static java.util.Collections.emptyList;

import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApiSimilarProductsFetcher implements SimilarProductsFetcher {

  private final RestClient restClient;

  public ApiSimilarProductsFetcher(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public List<String> findSimilarProductIds(String productId) {
    String[] productIds =
        restClient
            .get()
            .uri("/product/{productId}/similarids", productId)
            .retrieve()
            .body(String[].class);

    return productIds == null ? emptyList() : List.of(productIds);
  }
}
