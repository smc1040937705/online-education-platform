package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.WishlistDTO;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<WishlistDTO>> getUserWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WishlistDTO> wishlist = wishlistService.getUserWishlist(userPrincipal.getId());
        return ApiResponse.success(wishlist);
    }

    @PostMapping
    public ApiResponse<WishlistDTO> addToWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long courseId) {
        WishlistDTO wishlist = wishlistService.addToWishlist(userPrincipal.getId(), courseId);
        return ApiResponse.success(wishlist);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = wishlistService.removeFromWishlist(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }
}
