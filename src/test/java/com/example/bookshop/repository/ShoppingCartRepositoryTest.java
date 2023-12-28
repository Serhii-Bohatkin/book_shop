package com.example.bookshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-shoppingcart-cartitem-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-shoppingcart-cartitem-test.sql";
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Verify findShoppingCartByUserId() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findShoppingCartByUserId_ValidId_ShouldReturnShoppingCart() {
        ShoppingCart expected = createShoppingCart();
        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUserId(1L).get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify findShoppingCartByUserId() method returns an empty Optional with the "
            + "user ID not existing")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findShoppingCartByUserId_WithNonExistingUserId_ShouldReturnEmptyOptional() {
        Optional<ShoppingCart> actual =
                shoppingCartRepository.findShoppingCartByUserId(Long.MAX_VALUE);
        assertThat(actual).isEqualTo(Optional.empty());
    }

    private ShoppingCart createShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        return shoppingCart;
    }
}
