package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("reviews")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Long orderId;

    private Integer rating;

    private String content;

    private String images;

    private Integer status;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Product product;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
