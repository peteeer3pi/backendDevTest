# Backend dev technical test
We want to offer a new feature to our customers showing similar products to the one they are currently seeing. To do this we agreed with our front-end applications to create a new REST API operation that will provide them the product detail of the similar products for a given one. [Here](./similarProducts.yaml) is the contract we agreed.

We already have an endpoint that provides the product Ids similar for a given one. We also have another endpoint that returns the product detail by product Id. [Here](./existingApis.yaml) is the documentation of the existing APIs.

**Create a Spring boot application that exposes the agreed REST API on port 5000.**

![Diagram](./assets/diagram.jpg "Diagram")

Note that _Test_ and _Mocks_ components are given, you must only implement _yourApp_.

## Testing and Self-evaluation
You can run the same test we will put through your application. You just need to have docker installed.

First of all, you may need to enable file sharing for the `shared` folder on your docker dashboard -> settings -> resources -> file sharing.

Then you can start the mocks and other needed infrastructure with the following command.
```
docker compose up -d simulado influxdb grafana
```
Check that mocks are working with a sample request to [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

To execute the test run:
```
docker compose run --rm k6 run scripts/test.js
```
Browse [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) to view the results.

## Evaluation
The following topics will be considered:
- Code clarity and maintainability
- Performance
- Resilience

### Performance

Similar product details are fetched concurrently using Java virtual threads.

This allows independent external requests to run in parallel while preserving the similarity order in the final response.

The application also uses configurable connection and read timeouts for external API calls.

Default values:

    products.api.connect-timeout=2s
    products.api.read-timeout=2s

### Resilience

The external API is treated as an unreliable dependency.

The application:

- Uses connection and read timeouts to avoid waiting indefinitely.
- Handles products that no longer exist by ignoring them in the final result.
- Returns `404` when the requested product itself does not exist.
- Propagates external API failures as an internal server error instead of returning invalid product data.

## Configuration

The application configuration is defined in `src/main/resources/application.properties` and can be overridden using environment variables.

| Property | Environment variable | Default | Description |
|---|---|---|---|
| `server.port` | `SERVER_PORT` | `5000` | Port used by the application. |
| `products.api.base-url` | `PRODUCTS_API_BASE_URL` | `http://localhost:3001` | Base URL of the external product API. |
| `products.api.connect-timeout` | `PRODUCTS_API_CONNECT_TIMEOUT` | `2s` | Maximum time allowed to establish a connection. |
| `products.api.read-timeout` | `PRODUCTS_API_READ_TIMEOUT` | `2s` | Maximum time allowed waiting for a response from the external API. |

For example:

    SERVER_PORT=5000
    PRODUCTS_API_BASE_URL=http://localhost:3001
    PRODUCTS_API_CONNECT_TIMEOUT=2s
    PRODUCTS_API_READ_TIMEOUT=2s

Spring Boot automatically maps these environment variables to the corresponding application properties.


### Testing

The project contains unit tests for the domain use case and infrastructure adapters, controller tests, and Cucumber acceptance tests.

Run the complete test suite with:

    ./mvnw clean test

Check code formatting with:

    ./mvnw spotless:check

Apply formatting with:

    ./mvnw spotless:apply

## Project structure

The main application code is organized into domain and infrastructure layers:

    src/main/java/com/itx/similarproducts/
    ├── domain/
    │   ├── exception/
    │   ├── model/
    │   ├── service/
    │   └── usecase/
    └── infrastructure/
        ├── config/
        ├── controller/
        ├── dto/
        ├── exception/
        ├── mapper/
        └── service/

The domain layer contains the business logic and contracts required by the use case, while the infrastructure layer provides the concrete implementations for HTTP communication and application delivery.

