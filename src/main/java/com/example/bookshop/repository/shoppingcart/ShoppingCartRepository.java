package com.example.bookshop.repository.shoppingcart;

import com.example.bookshop.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = "cartItems.book")
    Optional<ShoppingCart> findShoppingCartByUserId(Long userId);
}
