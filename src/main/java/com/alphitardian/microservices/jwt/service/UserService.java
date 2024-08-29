package com.alphitardian.microservices.jwt.service;

import com.alphitardian.microservices.jwt.exception.UserNotFoundException;
import com.alphitardian.microservices.jwt.exception.ValidationFailureException;
import com.alphitardian.microservices.jwt.helper.ObjectMapperUtil;
import com.alphitardian.microservices.jwt.helper.UserHelper;
import com.alphitardian.microservices.jwt.model.Token;
import com.alphitardian.microservices.jwt.model.User;
import com.alphitardian.microservices.jwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserHelper userHelper;

  public Mono<ResponseEntity<User>> registerUser(User user) {
    user.setId(userHelper.generateId());

    if (userRepository.findByUsername(user.getUsername()) == null) {
      return userHelper.validateUser(user)
          .flatMap(validatedUser -> {
            validatedUser.setPassword(userHelper.encryptPassword(user.getPassword()));
            return Mono.just(validatedUser);
          })
          .doOnNext(unusedArgs -> userRepository.save(user))
          .map(unusedArgs -> ResponseEntity.status(HttpStatus.CREATED).body(user))
          .onErrorResume(ValidationFailureException.class, throwable -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getLocalizedMessage());
          });
    } else {
      return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
  }

  public Mono<User> findUserByUsername(String username) {
    return Mono.justOrEmpty(userRepository.findByUsername(username))
        .doOnNext(user -> log.info("Found user: {}", ObjectMapperUtil.writeValueAsString(user)))
        .switchIfEmpty(
            Mono.defer(() -> Mono.error(
                new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found in Database", username)
            ))
        );
  }

  public Mono<ResponseEntity<Token>> login(User user) {
    if (userRepository.findByUsername(user.getUsername()) == null) {
      return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    return findUserByUsername(user.getUsername())
        .flatMap(userResponse -> userHelper.validateUserPassword(userResponse, user.getPassword()))
        .flatMap(userResponse -> userHelper.generateToken(userResponse))
        .map(token -> ResponseEntity.status(HttpStatus.OK).body(token))
        .onErrorReturn(ValidationFailureException.class, ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  public Mono<ResponseEntity<List<User>>> getAllUser() {
    return Mono.fromCallable(() -> userRepository.findAll())
        .doOnNext(books -> log.info("User available: {}", ObjectMapperUtil.writeValueAsString(books)))
        .map(users -> ResponseEntity.status(HttpStatus.OK).body(users));
  }

  public Mono<Boolean> validateToken(String token) {
    return Mono.just(token)
        .map(accessToken -> accessToken.substring(7))
        .doOnNext(accessToken -> userHelper.validateJwtToken(accessToken))
        .map(accessToken -> userHelper.getUsernameFromToken(accessToken))
        .flatMap(this::findUserByUsername)
        .map(Objects::nonNull);
  }
}
