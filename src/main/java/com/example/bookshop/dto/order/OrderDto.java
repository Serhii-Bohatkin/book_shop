package com.example.bookshop.dto.order;

import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
