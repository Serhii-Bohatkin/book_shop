package com.example.bookshop;

import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookShopApplication {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Tsvety dlya Eldzhernona");
            book.setIsbn("9785699556991");
            book.setAuthor("Kiz Deniel");
            book.setPrice(BigDecimal.valueOf(29));
            bookService.save(bookMapper.toDto(book));
            System.out.println(bookService.findAll());
        };
    }*/
}
