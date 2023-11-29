package com.example.bookshop.dto.order;

import com.example.bookshop.model.Order;
import lombok.Data;

@Data
public class OrderStatusDto {
    private Order.Status status;
}
