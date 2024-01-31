package com.example.bookshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.book.BookSearchParametersDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.book.BookSpecificationBuilder;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    private Book book;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter and the Philosopher's Stone");
        book.setAuthor("Rowling, J.K");
        book.setIsbn("9781408855898");
        book.setPrice(BigDecimal.valueOf(17.83));
        book.setDescription("Harry Potter and the Philosopher's Stone is a fantasy "
                + "novel written by British author J. K. Rowling.");
        book.setCoverImage("https://en.wikipedia.org/wiki/File:"
                + "Harry_Potter_and_the_Philosopher%27s_Stone_Book_Cover.jpg");
    }

    @AfterEach
    void tearDown() {
        book = null;
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateBookRequestDto_ShouldReturnValidBookDto() {
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto();
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ShouldReturnAllBooksDto() {
        BookDto bookDto = createBookDto();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        List<BookDto> bookDtos = bookService.findAll(pageable);
        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.get(0)).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Verify findById() method works")
    public void findById_ValidId_ShouldReturnValidBookDto() {
        BookDto expected = createBookDto();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(book.getId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findById() method does not work for a book with a non-existent id")
    public void findById_WithNonExistingBookId_ShouldThrowException() {
        book.setId(Long.MAX_VALUE);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(book.getId()));
        String expected = "Can't get a book by id " + book.getId();
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify update() method works")
    public void update_ValidId_ShouldUpdateBook() {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        when(bookRepository.existsById(book.getId())).thenReturn(true);
        when(bookMapper.toModel(bookRequestDto)).thenReturn(book);
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(createBookDto());
        bookService.update(bookRequestDto, 1L);
    }

    @Test
    @DisplayName("Verify update() method does not work for a book with a non-existent id")
    public void update_WithNonExistingBookId_ShouldThrowException() {
        book.setId(Long.MAX_VALUE);
        CreateBookRequestDto requestDto = createBookRequestDto();
        when(bookRepository.existsById(book.getId())).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(requestDto, book.getId()));
        String expected = "Can't update a book with id " + book.getId();
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify delete() method works")
    public void delete_ValidId_ShouldDeleteBook() {
        bookService.deleteById(book.getId());
        verify(bookRepository, times(1)).deleteById(book.getId());
    }

    @Test
    @DisplayName("Verify findByCategoryId() method works")
    public void findByCategoryId_ValidIdAndPageable_ShouldReturnBookDtoWithoutCategoryIds() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        BookDtoWithoutCategoryIds dtoWithoutCategoryIds = createBookDtoWithoutCategoryIds();
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findAllByCategoryId(1L, pageable)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(dtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> withoutCategoryIdsDtos
                = bookService.findAllByCategoryId(1L, pageable);
        assertThat(withoutCategoryIdsDtos).hasSize(1);
        assertThat(withoutCategoryIdsDtos.get(0)).isEqualTo(dtoWithoutCategoryIds);
    }

    @Test
    @DisplayName("Verify search() method works")
    public void search_ValidParameters_ShouldReturnListBookDto() {
        BookSearchParametersDto searchParameters = createSearchParameters();
        Specification<Book> bookSpecification = Specification.where(null);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        BookDto bookDto = createBookDto();
        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.search(searchParameters, pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(bookDto);
    }

    private BookSearchParametersDto createSearchParameters() {
        return new BookSearchParametersDto(
                new String[]{book.getTitle()},
                new String[0],
                new String[0],
                new String[0],
                new String[0],
                new String[0]
        );
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(Set.of(1L));
    }

    private BookDto createBookDto() {
        return new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setCategoryIds(List.of(1L));
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        return new BookDtoWithoutCategoryIds()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());
    }
}
