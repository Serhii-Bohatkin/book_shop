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

    public OrderDto placeOrder(Authentication authentication,
                               OrderShippingAddressDto shippingAddressDto) {
        User user = getCurrentUser(authentication);
        ShoppingCart shoppingCart = findShoppingCart(user.getId());
        String shippingAddress = shippingAddressDto.getShippingAddress();
        Order order = createOrder(user, shoppingCart, shippingAddress);
        shoppingCartRepository.delete(shoppingCart);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto updateStatus(
            Long orderId,
            OrderStatusDto statusDto,
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);
        Order order = findOrder(orderId, user);
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
        Order order = findOrder(orderId, user);
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
                .filter(item -> item != null && Objects.equals(item.getId(), itemId))
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

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found"));
    }

    private ShoppingCart findShoppingCart(Long userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by user id " + userId));
    }

    private Order findOrder(Long id, User user) {
        return orderRepository.findByIdAndUserId(id, user.getId()).orElseThrow(()
                -> new EntityNotFoundException(
                "Can't find an order with id " + id));
    }

    private Set<OrderItem> getOrderItems(ShoppingCart shoppingCart, Order orderFromDb) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> orderItemMapper.toOrderItem(cartItem, orderFromDb))
                .map(orderItemRepository::save)
                .collect(Collectors.toSet());
    }

    private Order createOrder(User user, ShoppingCart shoppingCart, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setTotal(calculateAmount(shoppingCart));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        Order orderFromDb = orderRepository.save(order);
        Set<OrderItem> orderItems = getOrderItems(shoppingCart, orderFromDb);
        orderFromDb.setOrderItems(orderItems);
        return order;
    }
}
