package com.alphitardian.microservices.monitoring.helper;

import com.alphitardian.microservices.monitoring.exception.JsonParseFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String writeValueAsString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new JsonParseFailedException(e);
    }
  }
}
