package com.example.bookshop.repository.order;

import com.example.bookshop.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "orderItems")
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
