package com.example.bookshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookshop.dto.book.BookSearchParametersDto;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.book.BookSpecificationBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-book-category-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-book-category-test.sql";
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("Verify findAllByCategoryId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_ValidCategoryId_ShouldReturnOneBook() {
        Book expected = createBook();
        List<Book> books = bookRepository.findAllByCategoryId(1L, PageRequest.of(0, 10));
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify findAllByCategoryId() method returns
             an empty list with the category ID not existing
            """)
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByCategoryId_WithNonExistingCategoryId_ShouldReturnEmptyList() {
        Long nonExistingCategoryId = Long.MAX_VALUE;
        List<Book> books = bookRepository.findAllByCategoryId(nonExistingCategoryId,
                PageRequest.of(0, 10));
        assertThat(books).hasSize(0);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAll_ValidPageable_ShouldReturnTwoBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> actual = bookRepository.findAll(pageable);
        assertThat(actual).hasSize(2);
    }

    @Test
    @DisplayName("Verify findAll(Specification<Book> spec) method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAll_ValidSpecification_ShouldReturnOneBook() {
        BookSearchParametersDto searchParameters = createSearchParameters();
        Specification<Book> spec = bookSpecificationBuilder.build(searchParameters);
        List<Book> actual = bookRepository.findAll(spec, Pageable.unpaged()).stream().toList();
        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Verify findBiId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_ValidId_ShouldReturnValidBook() {
        Book expected = createBook();
        Book actual = bookRepository.findById(1L).get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findById() method returns an empty Optional with the book ID not existing")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_WithNonExistingBookId_ShouldReturnEmptyOptional() {
        Optional<Book> actual = bookRepository.findById(Long.MAX_VALUE);
        Optional<Object> expected = Optional.empty();
        assertThat(actual).isEqualTo(expected);
    }

    private BookSearchParametersDto createSearchParameters() {
        return new BookSearchParametersDto(
                new String[]{"Harry Potter and the Philosopher's Stone"},
                null,
                null,
                null,
                null,
                null
        );
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter and the Philosopher's Stone");
        book.setAuthor("Rowling, J.K");
        book.setIsbn("9781408855898");
        book.setPrice(BigDecimal.valueOf(17.83));
        Category category = new Category();
        category.setId(1L);
        category.setName("Fantasy");
        book.setCategories(Set.of(category));
        return book;
    }
}
