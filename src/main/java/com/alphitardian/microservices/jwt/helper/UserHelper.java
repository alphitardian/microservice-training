package com.alphitardian.microservices.jwt.helper;

import com.alphitardian.microservices.jwt.exception.ValidationFailureException;
import com.alphitardian.microservices.jwt.model.Token;
import com.alphitardian.microservices.jwt.model.User;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@Component
public class UserHelper {

  @Value("${token.secret}")
  private String tokenSecret;

  @Autowired
  PasswordEncoder passwordEncoder;

  public Long generateId() {
    return new Random().nextLong(10000);
  }

  public Mono<User> validateUser(User user) {
    if (user.getPassword().isEmpty()) {
      throw new ValidationFailureException(user);
    }

    return Mono.just(user);
  }

  public String encryptPassword(String normalPassword) {
    return passwordEncoder.encode(normalPassword);
  }

  public Mono<User> validateUserPassword(User userFromDb, String decryptedPassword) {
    if (!BCrypt.checkpw(decryptedPassword, userFromDb.getPassword())) {
      throw new ValidationFailureException(userFromDb);
    }

    return Mono.just(userFromDb);
  }

  public Mono<Token> generateToken(User user) {
    String jwtToken = Jwts.builder()
        .setClaims(new HashMap<>())
        .setSubject(user.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
        .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

    return Mono.just(
        Token.builder()
            .accessToken(jwtToken)
            .build()
    );
  }

  private Key getSignKey() {
    byte[] keyBytes= Decoders.BASE64.decode(tokenSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }
  private Claims extractAllClaims(String token) {
    return Jwts
        .parser()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Boolean validateJwtToken(String token) {
    try {
      final Claims claims = Jwts
          .parser()
          .setSigningKey(getSignKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      boolean isTokenExpired = claims.getExpiration().before(new Date());
      return !isTokenExpired;
    } catch (Exception ex) {
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    final Claims claims = Jwts
        .parser()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }
}
