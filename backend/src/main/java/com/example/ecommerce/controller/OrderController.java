package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Page<OrderDTO>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> orders = orderService.getUserOrders(userPrincipal.getId(), page, size);
        return ApiResponse.success(orders);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ApiResponse.success(order);
    }

    @PostMapping
    public ApiResponse<OrderDTO> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(userPrincipal.getId(), request);
        return ApiResponse.success(order);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Boolean> cancelOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = orderService.cancelOrder(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<Boolean> payOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = orderService.payOrder(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/receive")
    public ApiResponse<Boolean> receiveOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = orderService.receiveOrder(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<OrderDTO>> getOrdersByStatus(@PathVariable Integer status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ApiResponse.success(orders);
    }

    @PostMapping("/{id}/ship")
    public ApiResponse<Boolean> shipOrder(@PathVariable Long id) {
        boolean result = orderService.shipOrder(id);
        return ApiResponse.success(result);
    }
}
