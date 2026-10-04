package com.itx.similarproducts.infrastructure.service;

import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.infrastructure.dto.ProductResponse;
import com.itx.similarproducts.infrastructure.exception.ApiProductFetcherException;
import com.itx.similarproducts.infrastructure.mapper.ProductMapper;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApiProductFetcher implements ProductFetcher {

  private final RestClient restClient;
  private final ProductMapper productMapper;

  public ApiProductFetcher(RestClient restClient, ProductMapper productMapper) {
    this.restClient = restClient;
    this.productMapper = productMapper;
  }

  @Override
  public Optional<Product> findById(String productId) {
    return restClient
        .get()
        .uri("/product/{productId}", productId)
        .exchange(
            (request, response) -> {
              if (response.getStatusCode().value() == 404) {
                return Optional.empty();
              }

              if (response.getStatusCode().isError()) {
                throw new ApiProductFetcherException(response.getStatusCode());
              }

              ProductResponse productResponse = response.bodyTo(ProductResponse.class);

              return Optional.ofNullable(productResponse).map(productMapper::toEntity);
            });
  }
}
