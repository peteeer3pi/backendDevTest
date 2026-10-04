package com.itx.similarproducts.application.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public class FindSimilarProductsSteps {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ObjectMapper objectMapper;

  private ResponseEntity<String> response;

  @When("I request similar products for product {string}")
  public void requestSimilarProducts(String productId) {
    response = restTemplate.getForEntity("/product/{productId}/similar", String.class, productId);
  }

  @Then("the response status should be {int}")
  public void responseStatusShouldBe(int expectedStatus) {
    assertEquals(expectedStatus, response.getStatusCode().value());
  }

  @Then("the response should contain the similar products")
  public void responseShouldContainSimilarProducts(DataTable dataTable) throws IOException {
    assertNotNull(response.getBody());

    List<Map<String, Object>> actualProducts =
        objectMapper.readValue(response.getBody(), new TypeReference<>() {});

    List<Map<String, String>> expectedProducts = dataTable.asMaps(String.class, String.class);

    assertEquals(expectedProducts.size(), actualProducts.size());

    for (int i = 0; i < expectedProducts.size(); i++) {
      assertProductMatches(expectedProducts.get(i), actualProducts.get(i));
    }
  }

  private void assertProductMatches(Map<String, String> expected, Map<String, Object> actual) {
    assertEquals(expected.get("id"), actual.get("id"));
    assertEquals(expected.get("name"), actual.get("name"));
    assertEquals(
        Double.parseDouble(expected.get("price")), ((Number) actual.get("price")).doubleValue());
    assertEquals(Boolean.parseBoolean(expected.get("availability")), actual.get("availability"));
  }
}
