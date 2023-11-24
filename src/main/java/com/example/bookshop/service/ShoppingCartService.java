package com.example.bookshop.service;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.cartitem.CartItemResponseDto;
import com.example.bookshop.dto.cartitem.UpdateCartItemDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(Authentication authentication);

    CartItemResponseDto addBooksToShoppingCart(Authentication authentication,
                                               CartItemRequestDto requestDto);

    CartItemResponseDto update(Authentication authentication,
                               Long cartItemId, UpdateCartItemDto updateCartItemDto);

    void deleteCartItemById(Long cartItemId);
}
