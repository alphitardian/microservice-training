package com.alphitardian.microservices.monitoring.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class RequestLoggingFilter implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    log.info("Request endpoint: {} {}", request.getMethod(), request.getRequestURL());
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    log.info("Response status: {}", response.getStatus());

    long startTime = (long) request.getAttribute("startTime");
    long endTime = System.currentTimeMillis();
    long requestTime = endTime - startTime;
    log.info("Request {} {} ended in {} ms", request.getMethod(), request.getRequestURL(), requestTime);
  }
}
