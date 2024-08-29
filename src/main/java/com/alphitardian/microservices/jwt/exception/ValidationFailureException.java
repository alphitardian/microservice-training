package com.alphitardian.microservices.jwt.exception;

import com.alphitardian.microservices.jwt.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationFailureException extends RuntimeException {

  public ValidationFailureException(User user) {
    log.error("Validation error, please check your request: {}", user);
  }
}
