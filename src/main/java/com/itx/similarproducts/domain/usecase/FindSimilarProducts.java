package com.itx.similarproducts.domain.usecase;

import com.itx.similarproducts.domain.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import java.util.List;
import java.util.Optional;

public class FindSimilarProducts {

  private final SimilarProductsFetcher similarProductsFetcher;
  private final ProductFetcher productFetcher;

  public FindSimilarProducts(
      SimilarProductsFetcher similarProductsFetcher, ProductFetcher productFetcher) {
    this.similarProductsFetcher = similarProductsFetcher;
    this.productFetcher = productFetcher;
  }

  public List<Product> findSimilarProducts(String productId) {
    productFetcher.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

    return similarProductsFetcher.findSimilarProductIds(productId).stream()
        .map(productFetcher::findById)
        .flatMap(Optional::stream)
        .toList();
  }
}
