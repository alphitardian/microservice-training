package com.alphitardian.microservices.jwt.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class UserNotFoundException extends ResponseStatusException {

  public UserNotFoundException(HttpStatus httpStatus, String message, String username) {
    super(httpStatus, message);
    log.error("User with id {} not found", username);
  }
}
