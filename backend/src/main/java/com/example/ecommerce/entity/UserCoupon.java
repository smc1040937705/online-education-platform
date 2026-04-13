package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_coupons")
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long couponId;

    private Integer status;

    private LocalDateTime usedTime;

    private Long orderId;

    @TableField(exist = false)
    private Coupon coupon;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
