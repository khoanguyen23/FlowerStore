package com.uit.flowerstore.controller;

import com.uit.flowerstore.domain.CartItem;
import com.uit.flowerstore.services.CartItemService;
import com.uit.flowerstore.services.FlowerService;
import com.uit.flowerstore.services.ShoppingCartService;
import com.uit.flowerstore.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;
    private final FlowerService flowerService;
    private final ShoppingCartService shoppingCartService;
    @Autowired
    public CartItemController(CartItemService cartItemService,FlowerService flowerService,ShoppingCartService shoppingCartService) {
        this.cartItemService = cartItemService;
        this.flowerService = flowerService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/shopping-cart")
    public ResponseEntity<List<CartItem>> getCartItemsByShoppingCart(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<CartItem> cartItems = cartItemService.getCartItemsByShoppingCart(userDetails.getShoppingCart());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@RequestBody CartItem cartItem, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CartItem createdCartItem = cartItemService.createCartItem(cartItem, userDetails.getShoppingCart(),flowerService.getFlowerById(cartItem.getFlower().getId()));
        shoppingCartService.updateShoppingCart(userDetails.getShoppingCart(), userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCartItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable("id") Long id, @RequestBody CartItem updatedCartItem, Authentication authentication) {
    	UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        CartItem cartItem = cartItemService.updateCartItem(id, updatedCartItem, flowerService);
        shoppingCartService.updateShoppingCart(userDetails.getShoppingCart(), userDetails);
        if (cartItem != null) {
            return ResponseEntity.ok(cartItem);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<CartItem>> deleteCartItem(@PathVariable("id") Long id,Authentication authentication) {
        cartItemService.deleteCartItem(id);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<CartItem> cartItems = cartItemService.getCartItemsByShoppingCart(userDetails.getShoppingCart());
        return ResponseEntity.ok(cartItems);
    }
    
}


