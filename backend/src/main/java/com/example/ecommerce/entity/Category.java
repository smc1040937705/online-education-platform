package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("categories")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private String icon;

    private Integer status;

    @TableField(exist = false)
    private List<Category> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
