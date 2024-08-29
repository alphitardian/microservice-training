package com.alphitardian.microservices.monitoring.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonParseFailedException extends RuntimeException {

  public JsonParseFailedException(Exception exception) {
    log.error("Failed to parse object with error {}", exception.getLocalizedMessage());
  }
}
