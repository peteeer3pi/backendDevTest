package com.itx.similarproducts.domain.service;

import java.util.List;

public interface SimilarProductsFetcher {
  List<String> findSimilarProductIds(String productId);
}
