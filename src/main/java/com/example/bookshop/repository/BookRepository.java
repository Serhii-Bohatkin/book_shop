package com.example.bookshop.repository;

import com.example.bookshop.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();

    Optional<Book> findBookById(Long id);
}
