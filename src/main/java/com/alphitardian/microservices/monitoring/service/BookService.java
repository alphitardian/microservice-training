package com.alphitardian.microservices.monitoring.service;

import com.alphitardian.microservices.monitoring.exception.BookNotFoundException;
import com.alphitardian.microservices.monitoring.exception.ValidationFailureException;
import com.alphitardian.microservices.monitoring.helper.BookHelper;
import com.alphitardian.microservices.monitoring.helper.ObjectMapperUtil;
import com.alphitardian.microservices.monitoring.model.Book;
import com.alphitardian.microservices.monitoring.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class BookService {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookHelper bookHelper;

  public Mono<List<Book>> getAllBooks() {
    return Mono.fromCallable(() -> bookRepository.findAll())
        .doOnNext(books -> log.info("Book available: {}", ObjectMapperUtil.writeValueAsString(books)));
  }

  public Mono<Book> findBookById(Long id) {
    return Mono.justOrEmpty(bookRepository.findById(id))
        .doOnNext(user -> log.info("Found book: {}", ObjectMapperUtil.writeValueAsString(user)))
        .switchIfEmpty(
            Mono.defer(() -> Mono.error(
                new BookNotFoundException(HttpStatus.NOT_FOUND, "Book not found in Database", id)
            ))
        );
  }

  public Mono<ResponseEntity<Book>> addBook(Book book) {
    book.setId(bookHelper.generateId());

    return Mono.just(book)
        .flatMap(request -> bookHelper.validateBook(request))
        .doOnNext(bookRepository::save)
        .doOnNext(updateBook -> log.info("Save book success with payload: {}", ObjectMapperUtil.writeValueAsString(book)))
        .map(updateBook -> ResponseEntity.status(HttpStatus.CREATED).body(updateBook))
        .onErrorResume(ValidationFailureException.class, throwable -> {
          log.error("Failed to save book with error {}", throwable.getLocalizedMessage());
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getLocalizedMessage());
        });
  }

  public Mono<Book> updateBook(Book updatedBook, Long id) {
    return findBookById(id)
        .flatMap(existingBook -> bookHelper.updateBook(existingBook, updatedBook))
        .flatMap(bookHelper::validateBook)
        .doOnNext(bookRepository::save)
        .doOnNext(existingBook -> log.info("Update book with id {}, success with payload: {}", id, ObjectMapperUtil.writeValueAsString(existingBook)))
        .onErrorResume(ValidationFailureException.class, throwable -> {
          log.error("Failed to update book with error {}", throwable.getLocalizedMessage());
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.getLocalizedMessage());
        });
  }

  public Mono<ResponseEntity<String>> deleteBook(Long id) {
    if (bookRepository.findById(id).isEmpty()) {
      return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with id " + id + " not found"));
    }

    bookRepository.deleteById(id);
    return Mono.just(id)
        .doOnNext(ids -> log.info("Delete book with id {} success", ids))
        .map(ids -> ResponseEntity.status(HttpStatus.OK).body("Book with id " + ids + " deleted"))
        .onErrorResume(NullPointerException.class, throwable -> {
          throw new BookNotFoundException(HttpStatus.NOT_FOUND, "Book not found in Database", id);
        });
  }
}
