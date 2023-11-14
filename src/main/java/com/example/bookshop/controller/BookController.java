package com.example.bookshop.controller;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.BookSearchParametersDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books", description = "Get a list of all available books. "
            + "Pagination: add a ? followed by the query {page}={value}&{size}={value} "
            + "For example: /books?page=0&size=10 "
            + "Sorting: add & followed by {sort}={field} or {sort}={field, DESC}")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get a book by id", description = "Get a book by id")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a book by id", description = "Create a book by id")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update book", description = "Update book by id")
    @PutMapping("/{id}")
    public BookDto updateBook(
            @RequestBody @Valid CreateBookRequestDto requestDto, @PathVariable Long id) {
        return bookService.update(requestDto, id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete book", description = "Delete a book by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @Operation(summary = "Search book", description
            = "Search books by {title}, {author}, {isbn}, {price} or {description}. "
            + "To start searching add a ? followed by the query {query}={value}. "
            + "If you want to chain several queries in the same call, use & followed by the query."
            + " Pagination: add a & followed by the query {page}={value}&{size}={value}"
            + "For example: /books/search"
            + "?titles=Tsvety dlya Eldzhernona&page=0&size=10 "
            + "Sorting: add & followed by {sort}={field} or {sort}={field, DESC}")
    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters, Pageable pageable) {
        return bookService.search(searchParameters, pageable);
    }
}
