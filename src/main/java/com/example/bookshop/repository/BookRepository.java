package com.example.bookshop.repository;

import com.example.bookshop.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List findAll();
}
