package com.itx.similarproducts.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ApiSimilarProductsFetcherTest {

  private MockRestServiceServer server;
  private ApiSimilarProductsFetcher fetcher;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();

    server = MockRestServiceServer.bindTo(builder).build();

    fetcher = new ApiSimilarProductsFetcher(builder.build());
  }

  @Test
  void shouldReturnSimilarProductIds() {
    server
        .expect(requestTo("/product/1/similarids"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(
                    """
                                        ["2", "3", "4"]
                                        """));

    List<String> result = fetcher.findSimilarProductIds("1");

    assertEquals(List.of("2", "3", "4"), result);

    server.verify();
  }

  @Test
  void shouldReturnEmptyListWhenThereAreNoSimilarProducts() {
    server
        .expect(requestTo("/product/1/similarids"))
        .andExpect(method(GET))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(APPLICATION_JSON)
                .body(
                    """
                                        []
                                        """));

    List<String> result = fetcher.findSimilarProductIds("1");

    assertEquals(List.of(), result);

    server.verify();
  }
}
