package com.example.bookshop;

import com.example.bookshop.model.Book;
import com.example.bookshop.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookShopApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Tsvety dlya Eldzhernona");
            book.setIsbn("9785699556991");
            book.setAuthor("Kiz Deniel");
            book.setPrice(BigDecimal.valueOf(29));
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
