package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

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

    @TableField(exist = false)
    private List<OrderItem> items;

    @TableField(exist = false)
    private User user;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
