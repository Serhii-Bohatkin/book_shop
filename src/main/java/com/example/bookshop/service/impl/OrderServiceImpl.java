package com.example.bookshop.service.impl;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderShippingAddressDto;
import com.example.bookshop.dto.order.OrderStatusDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.OrderItemMapper;
import com.example.bookshop.mapper.OrderMapper;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.order.OrderRepository;
import com.example.bookshop.repository.orderitem.OrderItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.repository.user.UserRepository;
import com.example.bookshop.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    public OrderDto createOrder(Authentication authentication,
                                OrderShippingAddressDto shippingAddressDto) {
        User user = getCurrentUser(authentication);
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by user id " + user.getId()));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setTotal(calculateAmount(shoppingCart));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddressDto.getShippingAddress());
        Order orderFromDb = orderRepository.save(order);
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> orderItemMapper.toOrderItem(cartItem, orderFromDb))
                .map(orderItemRepository::save)
                .collect(Collectors.toSet());
        orderFromDb.setOrderItems(orderItems);
        shoppingCartRepository.delete(shoppingCart);
        return orderMapper.toDto(orderRepository.save(orderFromDb));
    }

    @Override
    public OrderDto updateStatus(Long id, OrderStatusDto statusDto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Order order = orderRepository.findByIdAndUserId(id, user.getId()).orElseThrow(()
                -> new EntityNotFoundException("Can't find an order with id " + id));
        order.setStatus(statusDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getAll(Authentication authentication, Pageable pageable) {
        User user = getCurrentUser(authentication);
        return orderRepository.findAllByUserId(user.getId(), pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getAllOrderItems(
            Long orderId, Authentication authentication, Pageable pageable) {
        User user = getCurrentUser(authentication);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId()).orElseThrow(()
                -> new EntityNotFoundException("Can't find an order with id " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemByOrderIdAndItemId(
            Long orderId, Long itemId, Authentication authentication) {
        List<OrderItemDto> allOrderItems = getAllOrderItems(
                orderId, authentication, Pageable.unpaged());
        return allOrderItems.stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(()
                        -> new EntityNotFoundException("Can't find item with id " + itemId));
    }

    private BigDecimal calculateAmount(ShoppingCart shoppingCart) {
        double total = shoppingCart.getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice().doubleValue())
                .sum();
        return BigDecimal.valueOf(total);
    }

    User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found"));
    }
}
