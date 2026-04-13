package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    OrderDTO createOrder(Long userId, CreateOrderRequest request);

    OrderDTO getOrderById(Long id);

    Page<OrderDTO> getUserOrders(Long userId, int page, int size);

    List<OrderDTO> getOrdersByStatus(Integer status);

    boolean cancelOrder(Long id, Long userId);

    boolean payOrder(Long id, Long userId);

    boolean shipOrder(Long id);

    boolean receiveOrder(Long id, Long userId);
}
