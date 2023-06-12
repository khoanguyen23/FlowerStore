package com.uit.flowerstore.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uit.flowerstore.domain.ShoppingCart;
import com.uit.flowerstore.domain.User;
import com.uit.flowerstore.repository.ShoppingCartRepository;
import com.uit.flowerstore.repository.UserRepository;
import com.uit.flowerstore.security.services.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
    }

    public List<ShoppingCart> getUserShoppingCarts(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            return shoppingCartRepository.findAllByUserId(user.getId());
        }
        return Collections.emptyList();
    }

    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            shoppingCart.setUser(user);
            return shoppingCartRepository.save(shoppingCart);
        }
        throw new EntityNotFoundException("User not found");
    }

    public ShoppingCart getShoppingCartById(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            return shoppingCartRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        }
        throw new EntityNotFoundException("Không tìm thấy người dùng");
    }

    
    @Transactional
    public ShoppingCart updateShoppingCart(ShoppingCart updatedShoppingCart, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            ShoppingCart shoppingCart = user.getShoppingCart();
            if (shoppingCart != null) {
                shoppingCart.setGrandTotal(updatedShoppingCart.getGrandTotal());
                return shoppingCartRepository.save(shoppingCart);
            }
        }
        return null;
    }
}
