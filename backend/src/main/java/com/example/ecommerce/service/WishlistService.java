package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.WishlistDTO;
import com.example.ecommerce.entity.Wishlist;

import java.util.List;

public interface WishlistService extends IService<Wishlist> {

    WishlistDTO addToWishlist(Long userId, Long courseId);

    boolean removeFromWishlist(Long id, Long userId);

    List<WishlistDTO> getUserWishlist(Long userId);
}
