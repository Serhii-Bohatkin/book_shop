package com.example.bookshop.service;

import com.example.bookshop.dto.BookDto;
import com.example.bookshop.dto.BookSearchParametersDto;
import com.example.bookshop.dto.CreateBookRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.book.BookSpecificationBuilder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private final BookRepository bookRepository;
    @Autowired
    private final BookMapper bookMapper;
    @Autowired
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get a book by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(CreateBookRequestDto requestDto, Long id) {
        Book bookFromDB = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't update a book with id " + id));
        bookFromDB.setTitle(requestDto.getTitle());
        bookFromDB.setAuthor(requestDto.getAuthor());
        bookFromDB.setIsbn(requestDto.getIsbn());
        bookFromDB.setPrice(requestDto.getPrice());
        bookFromDB.setDescription(requestDto.getDescription());
        bookFromDB.setCoverImage(requestDto.getCoverImage());
        return bookMapper.toDto(bookRepository.save(bookFromDB));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParameters) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
