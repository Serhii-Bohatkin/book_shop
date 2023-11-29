package com.example.bookshop.mapper;

import com.example.bookshop.config.MapperConfig;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    default OrderItem toOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        return orderItem;
    }
}
