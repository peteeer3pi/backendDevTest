Feature: Find similar products
  As a client of the product API
  I want to retrieve the products similar to a given product
  So that I can display relevant product recommendations

  Scenario: Find similar products for an existing product
    When I request similar products for product "1"
    Then the response status should be 200
    And the response should contain the similar products
      | id | name   | price | availability |
      | 2  | Dress  | 19.99 | true         |
      | 3  | Blazer | 29.99 | false        |
      | 4  | Boots  | 39.99 | true         |
    
  Scenario: Find similar products for a product that does not exist
    When I request similar products for product "999"
    Then the response status should be 404

  Scenario: Find similar products when the product API fails
    When I request similar products for product "6"
    Then the response status should be 500

  Scenario: Find similar products when the product API times out
    When I request similar products for product "10000"
    Then the response status should be 500