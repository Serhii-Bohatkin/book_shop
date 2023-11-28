package com.example.bookshop.service;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderShippingAddressDto;
import com.example.bookshop.dto.order.OrderStatusDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {
    OrderDto createOrder(Authentication authentication,
                         OrderShippingAddressDto shippingAddressDto);

    OrderDto updateStatus(Long orderId, OrderStatusDto statusDto, Authentication authentication);

    List<OrderDto> getAll(Authentication authentication, Pageable pageable);

    List<OrderItemDto> getAllOrderItems(
            Long orderId, Authentication authentication, Pageable pageable);

    OrderItemDto getItemByOrderIdAndItemId(
            Long orderId, Long itemId, Authentication authentication);
}
