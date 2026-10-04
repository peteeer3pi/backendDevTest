package com.itx.similarproducts.infrastructure.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.itx.similarproducts.domain.exception.ProductNotFoundException;
import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.domain.usecase.FindSimilarProducts;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  private MockMvc mockMvc;

  @Mock private FindSimilarProducts findSimilarProducts;

  @BeforeEach
  void setUp() {
    ProductController controller = new ProductController(findSimilarProducts);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ProductExceptionHandler())
            .build();
  }

  @Nested
  class WhenSimilarProductsExist {

    @BeforeEach
    void setUp() {
      when(findSimilarProducts.findSimilarProducts("1"))
          .thenReturn(
              List.of(aProduct("2", "product-2", 20, true), aProduct("3", "product-3", 30, false)));
    }

    @Test
    void shouldReturnSimilarProducts() throws Exception {
      mockMvc
          .perform(get("/product/1/similar"))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$[0].id").value("2"))
          .andExpect(jsonPath("$[0].name").value("product-2"))
          .andExpect(jsonPath("$[0].price").value(20))
          .andExpect(jsonPath("$[0].availability").value(true))
          .andExpect(jsonPath("$[1].id").value("3"))
          .andExpect(jsonPath("$[1].name").value("product-3"))
          .andExpect(jsonPath("$[1].price").value(30))
          .andExpect(jsonPath("$[1].availability").value(false));
    }
  }

  @Nested
  class WhenThereAreNoSimilarProducts {

    @BeforeEach
    void setUp() {
      when(findSimilarProducts.findSimilarProducts("1")).thenReturn(List.of());
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
      mockMvc
          .perform(get("/product/1/similar"))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(content().json("[]"));
    }
  }

  @Nested
  class WhenProductDoesNotExist {

    @BeforeEach
    void setUp() {
      when(findSimilarProducts.findSimilarProducts("1"))
          .thenThrow(new ProductNotFoundException("1"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
      mockMvc.perform(get("/product/1/similar")).andExpect(status().isNotFound());
    }
  }

  private Product aProduct(String id, String name, int price, boolean availability) {
    return Product.builder()
        .id(id)
        .name(name)
        .price(BigDecimal.valueOf(price))
        .availability(availability)
        .build();
  }
}
