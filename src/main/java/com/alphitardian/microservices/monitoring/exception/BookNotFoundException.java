package com.alphitardian.microservices.monitoring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class BookNotFoundException extends ResponseStatusException {

  public BookNotFoundException(HttpStatus httpStatus, String message, Long id) {
    super(httpStatus, message);
    log.error("Book with id {} not found", id);
  }
}
