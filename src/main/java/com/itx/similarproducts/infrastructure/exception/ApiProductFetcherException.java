package com.itx.similarproducts.infrastructure.exception;

import org.springframework.http.HttpStatusCode;

public class ApiProductFetcherException extends RuntimeException {

  public ApiProductFetcherException(HttpStatusCode statusCode) {
    super("Error fetching product from API: " + statusCode);
  }

  public ApiProductFetcherException(Throwable cause) {
    super("Error communicating with product API", cause);
  }
}
