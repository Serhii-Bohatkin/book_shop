package com.example.bookshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderShippingAddressDto;
import com.example.bookshop.dto.order.OrderStatusDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.OrderItemMapper;
import com.example.bookshop.mapper.OrderMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.order.OrderRepository;
import com.example.bookshop.repository.orderitem.OrderItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.repository.user.UserRepository;
import com.example.bookshop.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private OrderServiceImpl orderService;
    private Order order;
    private User user;
    private OrderItem orderItem;
    private Book book;
    private ShoppingCart shoppingCart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = createUser();
        book = createBook();
        orderItem = createOrderItem(book);
        order = createOrder(user, orderItem);
        cartItem = createCartItem(shoppingCart, book);
        shoppingCart = createShoppingCart(user, cartItem);
    }

    @AfterEach
    void tearDown() {
        order = null;
        user = null;
        orderItem = null;
        book = null;
    }

    @Test
    @DisplayName("Verify placeOrder() method works")
    public void placeOrder_ValidRequestDto_ShouldReturnOrderDto() {

        OrderShippingAddressDto addressDto = new OrderShippingAddressDto();
        addressDto.setShippingAddress("St. Main-Street 1");
        OrderDto expected = createOrderDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemMapper.toOrderItem(cartItem, order)).thenReturn(orderItem);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderMapper.toDto(order)).thenReturn(expected);
        OrderDto actual = orderService.placeOrder(authentication, addressDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify updateStatus() method works")
    public void updateStatus_ValidId_ShouldUpdateOrderStatus() {
        OrderStatusDto statusDto = new OrderStatusDto();
        statusDto.setStatus(Order.Status.DELIVERED);
        OrderDto expected = createOrderDto().setStatus(Order.Status.DELIVERED);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(order.getId(), user.getId()))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);
        OrderDto actual = orderService.updateStatus(order.getId(), statusDto, authentication);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify updateStatus() method doesn't work if order non exist")
    public void updateStatus_NonExistOrder_Exception() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(Long.MAX_VALUE, user.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.updateStatus(Long.MAX_VALUE, new OrderStatusDto(),
                        authentication));
        String expected = "Can't find an order with id " + Long.MAX_VALUE;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getAll() method works")
    public void getAll_ValidPageable_ReturnListOrderDtos() {
        List<OrderDto> expected = List.of(createOrderDto());
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(user.getId(), Pageable.unpaged()))
                .thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(createOrderDto());
        List<OrderDto> actual = orderService.getAll(authentication, Pageable.unpaged());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getAllOrderItems() method works")
    public void getAllOrderItems_ValidOrderId_ReturnListOrderItemDto() {
        List<OrderItemDto> expected = List.of(createOrderItemDto());
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(order.getId(), user.getId()))
                .thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(createOrderItemDto());
        List<OrderItemDto> actual = orderService.getAllOrderItems(order.getId(), authentication,
                Pageable.unpaged());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getAllOrderItems() method doesn't work if order non exist")
    public void getAllOrderItems_InvalidOrderId_Exception() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(Long.MAX_VALUE, user.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.getAllOrderItems(Long.MAX_VALUE, authentication,
                        Pageable.unpaged()));
        String expected = "Can't find an order with id " + Long.MAX_VALUE;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getItemByOrderIdAndItemId() method works")
    public void getItemByOrderIdAndItemId_ValidId_ShouldReturnOrderItemDto() {
        OrderItemDto expected = createOrderItemDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(order.getId(), user.getId()))
                .thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expected);
        OrderItemDto actual = orderService.getItemByOrderIdAndItemId(order.getId(),
                orderItem.getId(), authentication);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getItemByOrderIdAndItemId() method doesn't work if order non exist")
    public void getItemByOrderIdAndItemId_NonExistOrder_Exception() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(Long.MAX_VALUE, user.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.getItemByOrderIdAndItemId(Long.MAX_VALUE, orderItem.getId(),
                        authentication));
        String expected = "Can't find an order with id " + Long.MAX_VALUE;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getItemByOrderIdAndItemId() method doesn't work if OrderItem non exist")
    public void getItemByOrderIdAndItemId_NonExistOrderItem_Exception() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserId(order.getId(), user.getId()))
                .thenReturn(Optional.of(order));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.getItemByOrderIdAndItemId(order.getId(), Long.MAX_VALUE,
                        authentication));
        String expected = "Can't find item with id " + Long.MAX_VALUE;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    private OrderDto createOrderDto() {
        return new OrderDto()
                .setId(order.getId())
                .setUserId(user.getId())
                .setStatus(order.getStatus())
                .setTotal(BigDecimal.valueOf(18.0))
                .setOrderDate(LocalDateTime.parse("2023-12-25T10:37:33"))
                .setOrderItems(Set.of(createOrderItemDto()));
    }

    private OrderItemDto createOrderItemDto() {
        return new OrderItemDto()
                .setId(1L)
                .setBookId(book.getId())
                .setQuantity(1);
    }

    private ShoppingCart createShoppingCart(User user, CartItem cartItem) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));
        return shoppingCart;
    }

    private CartItem createCartItem(ShoppingCart shoppingCart, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(1);
        cartItem.setBook(book);
        return cartItem;
    }

    private Order createOrder(User user, OrderItem orderItem) {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.parse("2023-12-25T10:37:33"));
        order.setTotal(BigDecimal.valueOf(18.0));
        order.setStatus(Order.Status.NEW);
        order.setOrderItems(Set.of(orderItem));
        order.setShippingAddress("St. Main-Street 1");
        return order;
    }

    private OrderItem createOrderItem(Book book) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setQuantity(1);
        orderItem.setPrice(book.getPrice());
        orderItem.setQuantity(1);
        return orderItem;
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(BigDecimal.valueOf(18.0));
        return book;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        return user;
    }
}
