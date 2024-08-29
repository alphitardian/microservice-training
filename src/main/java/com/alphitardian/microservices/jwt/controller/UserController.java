package com.alphitardian.microservices.jwt.controller;

import com.alphitardian.microservices.jwt.model.Token;
import com.alphitardian.microservices.jwt.model.User;
import com.alphitardian.microservices.jwt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private UserService userService;

  private AuthenticationManager authenticationManager;

  @PostMapping("/auth/register")
  public Mono<ResponseEntity<User>> registerUser(@RequestBody User user) {
    return userService.registerUser(user);
  }

  @PostMapping("/auth/login")
  public Mono<ResponseEntity<Token>> login(@RequestBody User user) {
    return userService.login(user);
  }

  @GetMapping("/users")
  public Mono<ResponseEntity<? extends List>> getAllUser(@RequestHeader("Authorization") String accessToken) {
    return Mono.just(accessToken)
        .flatMap(userService::validateToken)
        .flatMap(isValidated -> {
          if (isValidated) {
            return userService.getAllUser();
          }
          return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.EMPTY_LIST));
        })
        .onErrorReturn(Exception.class, ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.EMPTY_LIST));
  }
}
