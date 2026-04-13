package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wishlists")
public class Wishlist {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long courseId;

    private Integer selected;

    @TableField(exist = false)
    private Course course;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
