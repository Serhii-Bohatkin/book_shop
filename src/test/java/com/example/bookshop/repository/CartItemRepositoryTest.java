package com.example.bookshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookshop.model.CartItem;
import com.example.bookshop.repository.cartitem.CartItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-shoppingcart-cartitem-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-shoppingcart-cartitem-test.sql";
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Verify findByIdAndShoppingCartId() method work")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_ValidId_ShouldReturnCartItem() {
        CartItem expected = createCartItem();
        CartItem actual = cartItemRepository.findByIdAndShoppingCartId(1L, 1L).get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findByIdAndShoppingCartId() method return empty Optional if CartItem id"
            + " not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_WithNonExistingItemId_ShouldReturnEmptyOptional() {
        Optional<CartItem> actual = cartItemRepository.findByIdAndShoppingCartId(Long.MAX_VALUE,
                1L);
        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Verify findByIdAndShoppingCartId() method return empty Optional if ShoppingCart"
            + " id not exist")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByIdAndShoppingCartId_NonExistingShoppingCartId_ShouldReturnEmptyOptional() {
        Optional<CartItem> actual = cartItemRepository.findByIdAndShoppingCartId(1L,
                Long.MAX_VALUE);
        assertThat(actual).isEqualTo(Optional.empty());
    }

    private CartItem createCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(1);
        return cartItem;
    }
}
