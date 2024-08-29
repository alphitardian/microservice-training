package com.alphitardian.microservices.monitoring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LogConfiguration implements WebMvcConfigurer {

  @Autowired
  private RequestLoggingFilter requestLoggingFilter;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestLoggingFilter);
  }
}
