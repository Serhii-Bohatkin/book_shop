package com.example.bookshop.service.impl;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.book.BookSearchParametersDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.book.BookSpecificationBuilder;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
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
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't update a book with id " + id);
        }
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        Set<Category> categories = new HashSet<>();
        for (Long categoryId : requestDto.getCategoryIds()) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new EntityNotFoundException("Category with id: " + id + " not found");
            }
            categories.add(new Category(categoryId));
        }
        book.setCategories(categories);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category with id: " + categoryId + " not found");
        }
        return bookRepository.findAllByCategoryId(categoryId, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
