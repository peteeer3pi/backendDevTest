package com.itx.similarproducts.infrastructure.controller;

import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.usecase.FindSimilarProducts;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

  private final FindSimilarProducts findSimilarProducts;

  public ProductController(FindSimilarProducts findSimilarProducts) {
    this.findSimilarProducts = findSimilarProducts;
  }

  @GetMapping("/{productId}/similar")
  public List<Product> findSimilarProducts(@PathVariable String productId) {
    return findSimilarProducts.findSimilarProducts(productId);
  }
}
