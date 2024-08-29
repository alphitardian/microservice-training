package com.alphitardian.microservices.monitoring.helper;

import com.alphitardian.microservices.monitoring.exception.ValidationFailureException;
import com.alphitardian.microservices.monitoring.model.Book;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
public class BookHelper {

  public Long generateId() {
    return new Random().nextLong(10000);
  }

  public Mono<Book> validateBook(Book book) {
    if (book.getAuthor() == null || book.getAuthor().isEmpty()
        || book.getTitle() == null || book.getTitle().isEmpty()) {
      throw new ValidationFailureException(book);
    }

    if (book.getYear() < 0) {
      throw new ValidationFailureException(book);
    }

    return Mono.just(book);
  }

  public Mono<Book> updateBook(Book existingBook, Book updatedBook) {
    if (!existingBook.getTitle().equals(updatedBook.getTitle())) {
      existingBook.setTitle(updatedBook.getTitle());
    }

    if (!existingBook.getAuthor().equals(updatedBook.getAuthor())) {
      existingBook.setAuthor(updatedBook.getAuthor());
    }

    if (existingBook.getYear() != updatedBook.getYear()) {
      existingBook.setYear(updatedBook.getYear());
    }

    if (existingBook.getId() != null) {
      existingBook.setId(existingBook.getId());
    }

    return Mono.just(existingBook);
  }
}
