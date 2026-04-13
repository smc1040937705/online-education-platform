package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    List<CartDTO> getUserCarts(Long userId);

    CartDTO addToCart(Long userId, Long productId, Integer quantity);

    CartDTO updateCartQuantity(Long id, Long userId, Integer quantity);

    boolean deleteCart(Long id, Long userId);

    boolean selectCart(Long id, Long userId, Integer selected);

    boolean selectAllCart(Long userId, Integer selected);
}
