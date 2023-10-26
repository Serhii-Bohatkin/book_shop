package com.example.bookshop.service;

import com.example.bookshop.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List findAll();
}
