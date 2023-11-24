package com.example.bookshop.controller;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.cartitem.CartItemResponseDto;
import com.example.bookshop.dto.cartitem.UpdateCartItemDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get shopping cart",
            description = "Get an existing shopping cart or create a new")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication);
    }

    @Operation(summary = "Add books to shopping cart", description = "Add books to shopping cart")
    @PostMapping
    public CartItemResponseDto addBooksToShoppingCart(Authentication authentication,
            @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addBooksToShoppingCart(authentication, requestDto);
    }

    @Operation(summary = "Update info about  quantity books",
            description = "Update info about  quantity books by cart item id")
    @PutMapping("/cart-items/{cartItemId}")
    public CartItemResponseDto update(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemDto updateCartItemDto) {
        return shoppingCartService.update(authentication, cartItemId, updateCartItemDto);
    }

    @Operation(summary = "Delete book from shopping cart",
            description = "Delete book from shopping cart by id")
    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long cartItemId) {
        shoppingCartService.deleteCartItemById(cartItemId);
    }
}
