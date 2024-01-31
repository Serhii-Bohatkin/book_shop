package com.example.bookshop.dto.orderitem;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
