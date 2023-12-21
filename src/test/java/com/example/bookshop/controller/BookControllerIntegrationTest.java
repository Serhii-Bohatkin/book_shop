package com.example.bookshop.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-book-category-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-book-category-test.sql";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify createBook() method works")
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createHarryPotterBookDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        reflectionEquals(actual, expected, "id");
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify createBook() method doesn't work when book already existing")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createBook_BookAlreadyExist_Exception() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Duplicate entry");
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getAll() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_ValidPageable_ShouldReturnTwoBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(createHarryPotterBookDto());
        expected.add(createAndThenThereWereNoneBookDto());
        MvcResult result = mockMvc.perform(
                        get("/books")
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertThat(actual).hasSize(2);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getBookById() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBookById_ValidId_ShouldReturnOneBookDto() throws Exception {
        BookDto expected = createHarryPotterBookDto();
        MvcResult result = mockMvc.perform(
                        get("/books/" + expected.getId())
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("Verify findById() method doesn't work with non existing book")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_NonExistingBookId_Exception() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/books/" + Long.MAX_VALUE)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't get a book by id " + Long.MAX_VALUE);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify updateBook() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto()
                .setAuthor("J.K. Rowling");
        BookDto expected = createHarryPotterBookDto().setAuthor("J.K. Rowling");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put("/books/" + expected.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify updateBook() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateBook_BookNonExisting_Exception() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto()
                .setAuthor("J.K. Rowling");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put("/books/" + Long.MAX_VALUE)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't update a book with id " + Long.MAX_VALUE);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify deleteBook() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteBook_ValidBookId_Success() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser
    @Test
    @DisplayName("Verify search() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void search_ValidSearchParameterDto_ShouldReturnOneBook() throws Exception {
        BookDto book = createHarryPotterBookDto();
        List<BookDto> expected = List.of(book);
        MvcResult result = mockMvc.perform(
                        get("/books/search?titles=" + book.getTitle())
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertThat(actual).hasSize(1);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    private BookDto createHarryPotterBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("Rowling, J.K")
                .setIsbn("9781408855898")
                .setPrice(BigDecimal.valueOf(17.83))
                .setCategoryIds(List.of(1L));
    }

    private BookDto createAndThenThereWereNoneBookDto() {
        return new BookDto()
                .setId(2L)
                .setTitle("And Then There Were None")
                .setAuthor("Agatha Christie")
                .setIsbn("9780008123208")
                .setPrice(BigDecimal.valueOf(10.73))
                .setCategoryIds(List.of(2L));
    }

    private CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("Rowling, J.K")
                .setIsbn("9781408855898")
                .setPrice(BigDecimal.valueOf(17.83))
                .setCategoryIds(Set.of(1L));
    }
}
