package com.example.bookshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.cartitem.CartItemResponseDto;
import com.example.bookshop.dto.cartitem.UpdateCartItemDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.mapper.CartItemMapper;
import com.example.bookshop.mapper.ShoppingCartMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.cartitem.CartItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.repository.user.UserRepository;
import com.example.bookshop.service.impl.ShoppingCartServiceImpl;
import java.util.HashSet;
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
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    private ShoppingCart shoppingCart;
    private User user;
    private Book book;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        book = new Book();
        book.setId(1L);
        user.setEmail("user@gmail.com");
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(1);
        cartItem.setBook(book);
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));
    }

    @AfterEach
    void tearDown() {
        user = null;
        shoppingCart = null;
    }

    @Test
    @DisplayName("Verify getShoppingCart() method works if ShoppingCart already exist in DB")
    public void getShoppingCart_ShoppingCartAlreadyExistInDb_ShouldReturnShoppingCart() {
        ShoppingCartDto expected = createShoppingCartDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getShoppingCart() method works if ShoppingCart non exist in DB")
    public void getShoppingCart_ShoppingCartNonExistInDb_ShouldReturnShoppingCart() {
        ShoppingCartDto expected = createShoppingCartDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.empty());
        when(shoppingCartRepository.save(new ShoppingCart())).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify addBooksToShoppingCart() method works if Book already exist in"
            + " ShoppingCart")
    public void addBooksToShoppingCart_BookAlreadyExistInCart_ShouldReturnCartItemResponseDto() {
        CartItemRequestDto requestDto = createCartItemRequestDto();
        CartItemResponseDto expected = createCartItemResponseDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);
        CartItemResponseDto actual = shoppingCartService.addBooksToShoppingCart(authentication,
                requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify addBooksToShoppingCart() method works if Book non exist in ShoppingCart")
    public void addBooksToShoppingCart_BookNonExistInCart_ShouldReturnCartItemResponseDto() {
        shoppingCart.setCartItems(new HashSet<>());
        CartItemRequestDto requestDto = createCartItemRequestDto();
        CartItemResponseDto expected = createCartItemResponseDto();
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);
        CartItemResponseDto actual = shoppingCartService.addBooksToShoppingCart(authentication,
                requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify update() method works")
    public void update_ValidRequestDto_ShouldReturnCartItemResponseDto() {
        UpdateCartItemDto updateDto = new UpdateCartItemDto().setQuantity(10);
        CartItemResponseDto expected = createCartItemResponseDto();
        expected.setQuantity(updateDto.getQuantity());
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findShoppingCartByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);
        CartItemResponseDto actual = shoppingCartService.update(authentication, cartItem.getId(),
                updateDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify deleteCartItemById() method works")
    public void deleteCartItemById() {
        assertDoesNotThrow(() -> shoppingCartService.deleteCartItemById(cartItem.getId()));
    }

    private CartItemResponseDto createCartItemResponseDto() {
        return new CartItemResponseDto()
                .setId(cartItem.getId())
                .setBookId(book.getId())
                .setBookTitle(book.getTitle())
                .setQuantity(cartItem.getQuantity());
    }

    private CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto()
                .setBookId(book.getId())
                .setQuantity(1);
    }

    private ShoppingCartDto createShoppingCartDto() {
        return new ShoppingCartDto()
                .setId(shoppingCart.getId());
    }
}
