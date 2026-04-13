package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("carts")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private Integer selected;

    @TableField(exist = false)
    private Product product;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
