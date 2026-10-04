package com.itx.similarproducts.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.itx.similarproducts.domain.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindSimilarProductsTest {

  @Mock private SimilarProductsFetcher similarProductsFetcher;

  @Mock private ProductFetcher productFetcher;

  @InjectMocks private FindSimilarProducts usecase;

  @Nested
  class WhenSimilarProductsExist {

    private final String productId = "1";

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(productId)).thenReturn(Optional.of(aProduct(productId)));

      when(similarProductsFetcher.findSimilarProductIds(productId)).thenReturn(List.of("2", "3"));

      when(productFetcher.findById("2")).thenReturn(Optional.of(aProduct("2")));

      when(productFetcher.findById("3")).thenReturn(Optional.of(aProduct("3")));
    }

    @Test
    void shouldReturnSimilarProductsInSimilarityOrder() {
      List<Product> result = usecase.findSimilarProducts(productId);

      assertEquals(List.of(aProduct("2"), aProduct("3")), result);
    }
  }

  @Nested
  class WhenThereAreNoSimilarProducts {

    private final String productId = "1";

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(productId)).thenReturn(Optional.of(aProduct(productId)));

      when(similarProductsFetcher.findSimilarProductIds(productId)).thenReturn(List.of());
    }

    @Test
    void shouldReturnEmptyList() {
      List<Product> result = usecase.findSimilarProducts(productId);

      assertEquals(List.of(), result);
    }
  }

  @Nested
  class WhenSimilarProductDoesNotExist {

    private final String productId = "1";

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(productId)).thenReturn(Optional.of(aProduct(productId)));

      when(similarProductsFetcher.findSimilarProductIds(productId)).thenReturn(List.of("2", "3"));

      when(productFetcher.findById("2")).thenReturn(Optional.of(aProduct("2")));

      when(productFetcher.findById("3")).thenReturn(Optional.empty());
    }

    @Test
    void shouldIgnoreMissingSimilarProduct() {
      List<Product> result = usecase.findSimilarProducts(productId);

      assertEquals(List.of(aProduct("2")), result);
    }
  }

  @Nested
  class WhenProductDoesNotExist {

    private final String productId = "1";

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(productId)).thenReturn(Optional.empty());
    }

    @Test
    void shouldThrowProductNotFoundException() {
      assertThrows(ProductNotFoundException.class, () -> usecase.findSimilarProducts(productId));

      verifyNoInteractions(similarProductsFetcher);
    }
  }

  private Product aProduct(String id) {
    return new Product(id, String.format("product-%s", id), BigDecimal.valueOf(10), true);
  }
}
