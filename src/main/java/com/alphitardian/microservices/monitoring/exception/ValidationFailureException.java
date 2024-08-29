package com.alphitardian.microservices.monitoring.exception;

import com.alphitardian.microservices.monitoring.model.Book;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationFailureException extends RuntimeException {

  public ValidationFailureException(Book book) {
    log.error("Validation error, please check your request: {}", book);
  }
}
