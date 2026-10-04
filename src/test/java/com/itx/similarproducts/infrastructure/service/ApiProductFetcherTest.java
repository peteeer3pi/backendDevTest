package com.itx.similarproducts.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.infrastructure.dto.ProductResponse;
import com.itx.similarproducts.infrastructure.exception.ApiProductFetcherException;
import com.itx.similarproducts.infrastructure.mapper.ProductMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ApiProductFetcherTest {

  private MockRestServiceServer server;
  private ApiProductFetcher fetcher;

  @Mock private ProductMapper productMapper;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();

    server = MockRestServiceServer.bindTo(builder).build();

    fetcher = new ApiProductFetcher(builder.build(), productMapper);
  }

  @Nested
  class WhenProductExists {

    @BeforeEach
    void setUp() {
      when(productMapper.toEntity(any(ProductResponse.class)))
          .thenReturn(aProduct(BigDecimal.valueOf(10.50)));
    }

    @Test
    void shouldReturnProduct() {
      server
          .expect(requestTo("/product/1"))
          .andExpect(method(GET))
          .andRespond(
              withStatus(HttpStatus.OK)
                  .contentType(APPLICATION_JSON)
                  .body(
                      """
                                            {
                                                "id": "1",
                                                "name": "product-1",
                                                "price": 10.50,
                                                "availability": true
                                            }
                                            """));

      Optional<Product> result = fetcher.findById("1");

      assertEquals(Optional.of(aProduct(BigDecimal.valueOf(10.50))), result);

      server.verify();
    }
  }

  @Nested
  class WhenProductDoesNotExist {

    @Test
    void shouldReturnEmpty() {
      server
          .expect(requestTo("/product/1"))
          .andExpect(method(GET))
          .andRespond(withStatus(HttpStatus.NOT_FOUND));

      Optional<Product> result = fetcher.findById("1");

      assertEquals(Optional.empty(), result);

      server.verify();
    }
  }

  @Nested
  class WhenApiReturnsAnError {

    @Test
    void shouldThrowException() {
      server
          .expect(requestTo("/product/1"))
          .andExpect(method(GET))
          .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

      assertThrows(ApiProductFetcherException.class, () -> fetcher.findById("1"));

      server.verify();
    }
  }

  private Product aProduct(BigDecimal price) {
    return Product.builder().id("1").availability(true).name("product-1").price(price).build();
  }
}
