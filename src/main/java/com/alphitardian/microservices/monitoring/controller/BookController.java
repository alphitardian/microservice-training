package com.alphitardian.microservices.monitoring.controller;

import com.alphitardian.microservices.monitoring.model.Book;
import com.alphitardian.microservices.monitoring.helper.ObjectMapperUtil;
import com.alphitardian.microservices.monitoring.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/buku")
public class BookController {

  @Autowired
  private BookService bookService;

  @GetMapping
  public Mono<List<Book>> getAllBooks() {
    return bookService.getAllBooks();
  }

  @GetMapping("/{id}")
  public Mono<Book> getBookById(@PathVariable Long id) {
    log.info("Get book with id {}", id);

    return bookService.findBookById(id);
  }

  @PostMapping
  public Mono<ResponseEntity<Book>> addBook(@RequestBody Book book) {
    log.info("Save book with payload: {}", ObjectMapperUtil.writeValueAsString(book));

    return bookService.addBook(book);
  }

  @PutMapping("/{id}")
  public Mono<Book> updateBook(@RequestBody Book book, @PathVariable("id") Long id) {
    log.info("Update book with id: {}, with payload: {}", id, ObjectMapperUtil.writeValueAsString(book));

    return bookService.updateBook(book, id);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<String>> deleteBook(@PathVariable("id") Long id) {
    log.info("Delete book with id {}", id);

    return bookService.deleteBook(id);
  }
}
