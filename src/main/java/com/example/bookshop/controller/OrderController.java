package com.example.bookshop.controller;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderShippingAddressDto;
import com.example.bookshop.dto.order.OrderStatusDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get all orders", description = "Get all user orders")
    @GetMapping
    public List<OrderDto> getAll(Authentication authentication, Pageable pageable) {
        return orderService.getAll(authentication, pageable);
    }

    @Operation(summary = "Place order",
            description = "Enter your shipping address and place your order")
    @PostMapping
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid OrderShippingAddressDto shippingAddressDto) {
        return orderService.placeOrder(authentication, shippingAddressDto);
    }

    @Operation(summary = "Update order status",
            description = "Specify one of the following statuses: "
                    + "NEW, PROCESSED, SHIPPED, DELIVERED, CANCELED")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusDto statusDto,
            Authentication authentication
    ) {
        return orderService.updateStatus(id, statusDto, authentication);
    }

    @Operation(summary = "Get all items by order id", description = "Get all items by order id")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllItemsByOrderId(
            @PathVariable Long orderId, Authentication authentication, Pageable pageable) {
        return orderService.getAllOrderItems(orderId, authentication, pageable);
    }

    @Operation(summary = "Get item by id", description = "Get item by order id and item id")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getItemById(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        return orderService.getItemByOrderIdAndItemId(orderId, itemId, authentication);
    }
}
