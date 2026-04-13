package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<List<CartDTO>> getUserCarts(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CartDTO> carts = cartService.getUserCarts(userPrincipal.getId());
        return ApiResponse.success(carts);
    }

    @PostMapping
    public ApiResponse<CartDTO> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        CartDTO cart = cartService.addToCart(userPrincipal.getId(), productId, quantity);
        return ApiResponse.success(cart);
    }

    @PutMapping("/{id}")
    public ApiResponse<CartDTO> updateCartQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        CartDTO cart = cartService.updateCartQuantity(id, userPrincipal.getId(), params.get("quantity"));
        return ApiResponse.success(cart);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = cartService.deleteCart(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/select")
    public ApiResponse<Boolean> selectCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        boolean result = cartService.selectCart(id, userPrincipal.getId(), params.get("selected"));
        return ApiResponse.success(result);
    }

    @PostMapping("/select-all")
    public ApiResponse<Boolean> selectAllCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, Integer> params) {
        boolean result = cartService.selectAllCart(userPrincipal.getId(), params.get("selected"));
        return ApiResponse.success(result);
    }
}
