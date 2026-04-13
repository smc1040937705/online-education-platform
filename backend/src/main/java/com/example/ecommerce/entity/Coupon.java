package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupons")
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Integer type;

    private BigDecimal value;

    private BigDecimal minAmount;

    private Integer totalCount;

    private Integer remainCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
