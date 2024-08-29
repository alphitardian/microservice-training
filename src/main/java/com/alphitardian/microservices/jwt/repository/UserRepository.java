package com.alphitardian.microservices.jwt.repository;

import com.alphitardian.microservices.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);
}
