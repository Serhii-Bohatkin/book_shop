package com.example.bookshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.cartitem.CartItemResponseDto;
import com.example.bookshop.dto.cartitem.UpdateCartItemDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ShoppingCartControllerIntegrationTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-shoppingcart-cartitem-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-shoppingcart-cartitem-test.sql";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getShoppingCart() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getShoppingCart_ShouldReturnShoppingCart() throws Exception {
        ShoppingCartDto expected = createShoppingCartDto();
        MvcResult result = mockMvc.perform(get("/cart")).andExpect(status().isOk()).andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify addBooksToShoppingCart() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addBooksToShoppingCart_ValidRequestDto_Success() throws Exception {
        CartItemRequestDto requestDto = createCartItemRequestDto();
        CartItemResponseDto expected = createCartItemResponseDto();
        expected.setQuantity(requestDto.getQuantity());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CartItemResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartItemResponseDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify addBooksToShoppingCart() method doesn't work if request dto invalid")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addBooksToShoppingCart_InvalidRequestDto_BadRequest() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto().setBookId(Long.MIN_VALUE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify update() method  works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void update_ValidRequestDto_Success() throws Exception {
        UpdateCartItemDto updateDto = new UpdateCartItemDto().setQuantity(10);
        CartItemResponseDto expected = createCartItemResponseDto();
        expected.setQuantity(updateDto.getQuantity());
        String jsonRequest = objectMapper.writeValueAsString(updateDto);
        MvcResult result = mockMvc.perform(put("/cart/cart-items/{cartItemId}", expected.getId())
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CartItemResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CartItemResponseDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify update() method doesn't work if CartItem not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void update_NonExistingCartItem_NotFound() throws Exception {
        UpdateCartItemDto updateDto = new UpdateCartItemDto().setQuantity(10);
        CartItemResponseDto expected = createCartItemResponseDto();
        expected.setQuantity(updateDto.getQuantity());
        String jsonRequest = objectMapper.writeValueAsString(updateDto);
        MvcResult result = mockMvc.perform(put("/cart/cart-items/{cartItemId}", Long.MAX_VALUE)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't find cart item with id " + Long.MAX_VALUE);

    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify delete() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void delete_ValidId_NoContent() throws Exception {
        mockMvc.perform(delete("/cart/cart-items/{cartItemId}", 1L))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto()
                .setBookId(1L)
                .setQuantity(10);
    }

    private ShoppingCartDto createShoppingCartDto() {
        return new ShoppingCartDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(Set.of(createCartItemResponseDto(),
                        createSecondCartItemResponseDto()));
    }

    private CartItemResponseDto createCartItemResponseDto() {
        return new CartItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Harry Potter and the Philosopher's Stone")
                .setQuantity(1);
    }

    private CartItemResponseDto createSecondCartItemResponseDto() {
        return new CartItemResponseDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("And Then There Were None")
                .setQuantity(1);
    }
}
