package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.mapper.*;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        Address address = addressMapper.selectById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        String cartIdStr = request.getCartIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        List<Cart> carts = cartMapper.findByIdsAndUserId(cartIdStr, userId);

        if (carts.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("商品不存在或已下架: " + cart.getProductId());
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.findByUserIdAndCouponId(userId, request.getCouponId());
            if (userCoupon == null || userCoupon.getStatus() != 0) {
                throw new RuntimeException("优惠券无效");
            }
            Coupon coupon = couponMapper.selectById(request.getCouponId());
            if (coupon == null || coupon.getStatus() != 1) {
                throw new RuntimeException("优惠券无效");
            }
            if (totalAmount.compareTo(coupon.getMinAmount()) >= 0) {
                if (coupon.getType() == 1) {
                    discountAmount = coupon.getValue();
                } else {
                    discountAmount = totalAmount.multiply(coupon.getValue()).divide(new BigDecimal(100));
                }
                userCoupon.setStatus(1);
                userCouponMapper.updateById(userCoupon);
            }
        }

        BigDecimal freightAmount = totalAmount.compareTo(new BigDecimal(99)) >= 0 ? BigDecimal.ZERO : new BigDecimal(10);
        BigDecimal payAmount = totalAmount.add(freightAmount).subtract(discountAmount);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setFreightAmount(freightAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(request.getRemark());

        orderMapper.insert(order);

        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());

            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setProductPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItemMapper.insert(item);

            productMapper.deductStock(cart.getProductId(), cart.getQuantity());
            cartMapper.deleteById(cart.getId());
        }

        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDTO(order);
    }

    @Override
    public Page<OrderDTO> getUserOrders(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        Page<Order> orderPage = orderMapper.findByUserId(pageParam, userId);

        List<OrderDTO> dtoList = orderPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<OrderDTO> result = new Page<>(page, size);
        result.setTotal(orderPage.getTotal());
        result.setRecords(dtoList);
        return result;
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(Integer status) {
        List<Order> orders = orderMapper.findByStatus(status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long id, Long userId) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许取消");
        }

        order.setStatus(-1);
        boolean result = updateById(order);

        List<OrderItem> items = orderItemMapper.findByOrderId(id);
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean payOrder(Long id, Long userId) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }

        int result = userMapper.deductBalance(userId, order.getPayAmount());
        if (result <= 0) {
            throw new RuntimeException("余额不足");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean shipOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许发货");
        }

        order.setStatus(2);
        order.setShipTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean receiveOrder(Long id, Long userId) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不允许收货");
        }

        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        return updateById(order);
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        }

        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        List<OrderItemDTO> itemDTOs = items.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
}
