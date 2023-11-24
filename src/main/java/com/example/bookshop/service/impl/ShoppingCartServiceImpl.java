package com.example.bookshop.service.impl;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.cartitem.CartItemResponseDto;
import com.example.bookshop.dto.cartitem.UpdateCartItemDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.exception.EntityNotFoundException;
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
import com.example.bookshop.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartMapper.toDto(findShoppingCartForCurrentUser(authentication));
    }

    @Override
    public CartItemResponseDto addBooksToShoppingCart(
            Authentication authentication,
            CartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = findShoppingCartForCurrentUser(authentication);
        Optional<CartItem> existingCartItem = shoppingCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(requestDto.getBookId()))
                .findFirst();
        CartItem cartItem = existingCartItem.orElseGet(()
                -> createNewCartItem(requestDto, shoppingCart));
        cartItem.setQuantity(requestDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemResponseDto update(
            Authentication authentication, Long cartItemId,
            UpdateCartItemDto updateCartItemDto) {
        ShoppingCart shoppingCart = findShoppingCartForCurrentUser(authentication);
        CartItem cartItem =
                cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Can't find cart item with id " + cartItemId));
        cartItem.setQuantity(updateCartItemDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteCartItemById(Long id) {
        cartItemRepository.deleteById(id);
    }

    private ShoppingCart findShoppingCartForCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found"));
        return shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseGet(() -> createNewShoppingCart(user));
    }

    private ShoppingCart createNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }

    private CartItem createNewCartItem(CartItemRequestDto requestDto,
                                       ShoppingCart shoppingCart) {
        Book book = bookRepository
                .findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book with id "
                                + requestDto.getBookId()));
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }
}
