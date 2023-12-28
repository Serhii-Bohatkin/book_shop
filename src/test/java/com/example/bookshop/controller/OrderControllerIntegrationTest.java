package com.example.bookshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderShippingAddressDto;
import com.example.bookshop.dto.order.OrderStatusDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-order-orderitem-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-order-orderitem-test.sql";
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
    @DisplayName("Verify getAll() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_ValidPageable_Success() throws Exception {
        List<OrderDto> expected = List.of(createOrderDto());
        MvcResult result = mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andReturn();
        OrderDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                OrderDto[].class);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify placeOrder() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void placeOrder_ValidAddressDto_Success() throws Exception {
        OrderDto expected = createOrderDto();
        OrderShippingAddressDto addressDto = new OrderShippingAddressDto();
        addressDto.setShippingAddress("St. Main-Street 1");
        String jsonRequest = objectMapper.writeValueAsString(addressDto);
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andReturn();
        OrderDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                OrderDto.class);
        EqualsBuilder.reflectionEquals(actual, expected, "id", "orderItems.id", "orderDate");
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify placeOrder() method doesn't work with empty addressDto")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void placeOrder_InvalidAddressDto_BadRequest() throws Exception {
        OrderShippingAddressDto addressDto = new OrderShippingAddressDto();
        addressDto.setShippingAddress("");
        String jsonRequest = objectMapper.writeValueAsString(addressDto);
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user@gmail.com", authorities = "ADMIN")
    @Test
    @DisplayName("Verify updateStatus() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateStatus_ValidAddressDto_Success() throws Exception {
        OrderStatusDto statusDto = new OrderStatusDto();
        statusDto.setStatus(Order.Status.DELIVERED);
        OrderDto expected = createOrderDto().setStatus(Order.Status.DELIVERED);
        String jsonRequest = objectMapper.writeValueAsString(statusDto);
        MvcResult result = mockMvc.perform(
                        patch("/orders/{id}", expected.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        OrderDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                OrderDto.class);
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
    }

    @WithMockUser(username = "user@gmail.com", authorities = "ADMIN")
    @Test
    @DisplayName("Verify updateStatus() doesn't work with invalid statusDto")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateStatus_InvalidAddressDto_Conflict() throws Exception {
        OrderStatusDto statusDto = new OrderStatusDto();
        statusDto.setStatus(null);
        String jsonRequest = objectMapper.writeValueAsString(statusDto);
        MvcResult result = mockMvc.perform(
                        patch("/orders/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andReturn();
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getAllItemsByOrderId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllItemsByOrderId_ValidId_Success() throws Exception {
        List<OrderItemDto> expected = List.of(createOrderItemDto());
        MvcResult result = mockMvc.perform(
                        get("/orders/{orderId}/items", 1L)
                )
                .andExpect(status().isOk())
                .andReturn();
        OrderItemDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                        OrderItemDto[].class);
        assertThat(Arrays.stream(actual).toList()).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getAllItemsByOrderId() method doesn't work if order not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllItemsByOrderId_OrderNotExist_NotFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't find an order with id " + Long.MAX_VALUE);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getItemById() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getItemById_ValidId_Success() throws Exception {
        OrderItemDto expected = createOrderItemDto();
        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items/{itemId}", 1L, 1L))
                .andExpect(status().isOk())
                .andReturn();
        OrderItemDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                OrderItemDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getItemById() method doesn't work if order not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getItemById_InvalidOrderId_NotFound() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/orders/{orderId}/items/{itemId}", Long.MAX_VALUE, 1L)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't find an order with id " + Long.MAX_VALUE);
    }

    @WithMockUser(username = "user@gmail.com")
    @Test
    @DisplayName("Verify getItemById() method doesn't work if order item not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getItemById_InvalidOrderItemId_NotFound() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/orders/{orderId}/items/{itemId}", 1L, Long.MAX_VALUE)
                )
                .andExpect(status().isNotFound())
                .andReturn();
        String actual = result.getResponse().getContentAsString();
        assertThat(actual).contains("Can't find item with id " + Long.MAX_VALUE);
    }

    private OrderDto createOrderDto() {
        return new OrderDto()
                .setId(1L)
                .setUserId(1L)
                .setStatus(Order.Status.NEW)
                .setTotal(BigDecimal.valueOf(18))
                .setOrderDate(LocalDateTime.parse("2023-12-25T10:37:33"))
                .setOrderItems(Set.of(createOrderItemDto()));
    }

    private OrderItemDto createOrderItemDto() {
        return new OrderItemDto()
                .setId(1L)
                .setBookId(1L)
                .setQuantity(1);
    }

}
