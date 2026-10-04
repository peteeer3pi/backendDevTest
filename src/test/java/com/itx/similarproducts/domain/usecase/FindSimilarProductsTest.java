package com.itx.similarproducts.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.itx.similarproducts.domain.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.service.ProductFetcher;
import com.itx.similarproducts.domain.service.SimilarProductsFetcher;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindSimilarProductsTest {

  @Mock private SimilarProductsFetcher similarProductsFetcher;

  @Mock private ProductFetcher productFetcher;

  private ExecutorService executorService;
  private FindSimilarProducts usecase;

  private static final String A_PRODUCT_ID = "1";

  @BeforeEach
  void setUp() {
    executorService = Executors.newVirtualThreadPerTaskExecutor();
    usecase = new FindSimilarProducts(similarProductsFetcher, productFetcher, executorService);
  }

  @AfterEach
  void tearDown() {
    executorService.close();
  }

  @Nested
  class WhenSimilarProductsExist {

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(A_PRODUCT_ID)).thenReturn(Optional.of(aProduct(A_PRODUCT_ID)));
      when(similarProductsFetcher.findSimilarProductIds(A_PRODUCT_ID))
          .thenReturn(List.of("2", "3"));
      when(productFetcher.findById("2")).thenReturn(Optional.of(aProduct("2")));
      when(productFetcher.findById("3")).thenReturn(Optional.of(aProduct("3")));
    }

    @Test
    void shouldReturnSimilarProductsInSimilarityOrder() {
      List<Product> result = usecase.findSimilarProducts(A_PRODUCT_ID);

      assertEquals(List.of(aProduct("2"), aProduct("3")), result);
    }
  }

  @Nested
  class WhenSimilarProductsAreFetchedInParallel {

    private CountDownLatch started;
    private CountDownLatch release;

    @BeforeEach
    void setUp() {
      started = new CountDownLatch(2);
      release = new CountDownLatch(1);

      when(productFetcher.findById(A_PRODUCT_ID)).thenReturn(Optional.of(aProduct(A_PRODUCT_ID)));
      when(similarProductsFetcher.findSimilarProductIds(A_PRODUCT_ID))
          .thenReturn(List.of("2", "3"));
      when(productFetcher.findById("2"))
          .thenAnswer(
              invocation -> {
                started.countDown();
                release.await();
                return Optional.of(aProduct("2"));
              });
      when(productFetcher.findById("3"))
          .thenAnswer(
              invocation -> {
                started.countDown();
                release.await();
                return Optional.of(aProduct("3"));
              });
    }

    @Test
    void shouldFetchSimilarProductsInParallel() throws InterruptedException {
      CompletableFuture<List<Product>> resultFuture =
          CompletableFuture.supplyAsync(() -> usecase.findSimilarProducts(A_PRODUCT_ID));

      boolean allRequestsStarted = started.await(1, TimeUnit.SECONDS);
      release.countDown();

      assertTrue(allRequestsStarted);

      List<Product> result = resultFuture.join();

      assertEquals(List.of(aProduct("2"), aProduct("3")), result);
    }
  }

  @Nested
  class WhenThereAreNoSimilarProducts {

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(A_PRODUCT_ID)).thenReturn(Optional.of(aProduct(A_PRODUCT_ID)));
      when(similarProductsFetcher.findSimilarProductIds(A_PRODUCT_ID)).thenReturn(List.of());
    }

    @Test
    void shouldReturnEmptyList() {
      List<Product> result = usecase.findSimilarProducts(A_PRODUCT_ID);

      assertEquals(List.of(), result);
    }
  }

  @Nested
  class WhenSimilarProductDoesNotExist {

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(A_PRODUCT_ID)).thenReturn(Optional.of(aProduct(A_PRODUCT_ID)));
      when(similarProductsFetcher.findSimilarProductIds(A_PRODUCT_ID))
          .thenReturn(List.of("2", "3"));
      when(productFetcher.findById("2")).thenReturn(Optional.of(aProduct("2")));
      when(productFetcher.findById("3")).thenReturn(Optional.empty());
    }

    @Test
    void shouldIgnoreMissingSimilarProduct() {
      List<Product> result = usecase.findSimilarProducts(A_PRODUCT_ID);

      assertEquals(List.of(aProduct("2")), result);
    }
  }

  @Nested
  class WhenProductDoesNotExist {

    @BeforeEach
    void setUp() {
      when(productFetcher.findById(A_PRODUCT_ID)).thenReturn(Optional.empty());
    }

    @Test
    void shouldThrowProductNotFoundException() {
      assertThrows(ProductNotFoundException.class, () -> usecase.findSimilarProducts(A_PRODUCT_ID));

      verifyNoInteractions(similarProductsFetcher);
    }
  }

  private Product aProduct(String id) {
    return Product.builder()
        .id(id)
        .name(String.format("product-%s", id))
        .price(BigDecimal.valueOf(10))
        .availability(true)
        .build();
  }
}
