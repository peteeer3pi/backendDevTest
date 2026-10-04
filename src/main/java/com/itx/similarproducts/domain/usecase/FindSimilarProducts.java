package com.itx.similarproducts.domain.usecase;

import com.itx.similarproducts.domain.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class FindSimilarProducts {

  private final SimilarProductsFetcher similarProductsFetcher;
  private final ProductFetcher productFetcher;
  private final ExecutorService executorService;

  public FindSimilarProducts(
      SimilarProductsFetcher similarProductsFetcher,
      ProductFetcher productFetcher,
      ExecutorService executorService) {
    this.similarProductsFetcher = similarProductsFetcher;
    this.productFetcher = productFetcher;
    this.executorService = executorService;
  }

  public List<Product> findSimilarProducts(String productId) {
    ensureProductExists(productId);

    List<String> similarProductIds = similarProductsFetcher.findSimilarProductIds(productId);

    return fetchProducts(similarProductIds);
  }

  private void ensureProductExists(String productId) {
    productFetcher.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
  }

  private List<Product> fetchProducts(List<String> productIds) {
    List<CompletableFuture<Optional<Product>>> futures =
        productIds.stream().map(this::fetchProductAsync).toList();

    return futures.stream().map(CompletableFuture::join).flatMap(Optional::stream).toList();
  }

  private CompletableFuture<Optional<Product>> fetchProductAsync(String productId) {
    return CompletableFuture.supplyAsync(() -> productFetcher.findById(productId), executorService);
  }
}
