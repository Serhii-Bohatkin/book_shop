package com.example.bookshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIntegrationTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-book-category-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-book-category-test.sql";
    private static final String DESCRIPTION = "Fantasy is a genre of speculative fiction "
            + "involving magical elements, typically set in a fictional universe and usually "
            + "inspired by mythology or folklore";
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
    @DisplayName("Verify createCategory() method works")
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        CategoryDto expected = createFantasyCategoryDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(actual, expected, "id");
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify createCategory() method doesn't work with empty name")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createCategory_CategoryAlreadyExist_Exception() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto().setName("");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("name must not be blank");
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getAll() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_ValidPageable_ShouldReturnTwoCategories() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(createFantasyCategoryDto());
        expected.add(createDetectiveCategoryDto());
        MvcResult result = mockMvc.perform(
                        get("/categories")
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                        CategoryDto[].class);
        assertThat(actual).hasSize(2);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getCategoryById() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getCategoryById_ValidId_ShouldReturnOneBookDto() throws Exception {
        CategoryDto expected = createFantasyCategoryDto();
        MvcResult result = mockMvc.perform(
                        get("/categories/" + expected.getId())
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getCategoryById() method doesn't work with non existing category")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getCategoryById_NonExistingCategoryId_Exception() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories/" + Long.MAX_VALUE)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't find a category with id " + Long.MAX_VALUE);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify updateCategory() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto =
                createCategoryRequestDto().setDescription(DESCRIPTION);
        CategoryDto expected = createFantasyCategoryDto().setDescription(DESCRIPTION);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put("/categories/" + expected.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify updateCategory() method doesn't work with a non existent category id")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCategory_NotExistingCategoryId_Exception() throws Exception {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put("/categories/" + Long.MAX_VALUE)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't update a category with id " + Long.MAX_VALUE);
    }

    @WithMockUser(value = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Verify deleteCategory() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCategory_ValidCategoryId_Success() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser
    @Test
    @DisplayName("Verify getBooksByCategoryId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByCategoryId_ValidCategoryId_ShouldReturnOneBookDto() throws Exception {
        BookDtoWithoutCategoryIds dto = createBookDtoWithoutCategory();
        List<BookDtoWithoutCategoryIds> expected = List.of(dto);
        MvcResult result = mockMvc.perform(
                        get("/categories/1/books")
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDtoWithoutCategoryIds[].class);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    @WithMockUser
    @Test
    @DisplayName("""
            Verify getBooksByCategoryId() method doesn't 
            work with a non existent category id
            """)
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getBooksByCategoryId_InValidCategoryId_Exception() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories/" + Long.MAX_VALUE + "/books")
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Category with id: " + Long.MAX_VALUE + " not found");
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategory() {
        return new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("Rowling, J.K")
                .setIsbn("9781408855898")
                .setPrice(BigDecimal.valueOf(17.83));
    }

    private CategoryDto createFantasyCategoryDto() {
        return new CategoryDto()
                .setId(1L)
                .setName("Fantasy");
    }

    private CategoryDto createDetectiveCategoryDto() {
        return new CategoryDto()
                .setId(2L)
                .setName("Detective");
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName("Fantasy");
    }
}
