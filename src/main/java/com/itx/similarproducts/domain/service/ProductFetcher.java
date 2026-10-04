package com.itx.similarproducts.domain.service;

import com.itx.similarproducts.domain.model.Product;
import java.util.Optional;

public interface ProductFetcher {
  Optional<Product> findById(String productId);
}
