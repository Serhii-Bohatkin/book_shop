package com.example.bookshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookshop.model.Order;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.order.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-order-orderitem-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-order-orderitem-test.sql";
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Verify findByIdAndUserId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndUserId_ValidId_ShouldReturnOrder() {
        Order expected = createOrder();
        Order actual = orderRepository.findByIdAndUserId(1L, 1L).get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findByIdAndUserId() method return empty Optional if order not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndUserId_OrderNotExist_EmptyOptional() {
        Optional<Order> actual = orderRepository.findByIdAndUserId(Long.MAX_VALUE, Long.MAX_VALUE);
        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Verify findAllByUserId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByUserId_ValidPageable_ShouldReturnOneOrder() {
        List<Order> expected = List.of(createOrder());
        List<Order> actual = orderRepository.findAllByUserId(1L, Pageable.unpaged());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findAllByUserId() method return empty list if user not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByUserId_NonExistOrderId_ShouldReturnEmptyList() {
        List<Order> actual = orderRepository.findAllByUserId(Long.MAX_VALUE, Pageable.unpaged());
        assertThat(actual).isEqualTo(Collections.emptyList());
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(createUser());
        order.setStatus(Order.Status.NEW);
        order.setTotal(BigDecimal.valueOf(18));
        order.setOrderDate(LocalDateTime.parse("2023-12-25T10:37:33"));
        order.setShippingAddress("St. Main-Street 1");
        return order;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        return user;
    }
}
