package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal freightAmount;
    private BigDecimal payAmount;
    private Integer payType;
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
}
