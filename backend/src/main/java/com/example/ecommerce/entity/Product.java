package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("products")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String detail;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private Long categoryId;

    private String mainImage;

    @TableField(exist = false)
    private List<String> subImages;

    private Integer status;

    private Integer isHot;

    private Integer isNew;

    private Integer isRecommend;

    @TableField(exist = false)
    private Category category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
