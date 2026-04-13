package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("courses")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String detail;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer enrollment;

    private Integer students;

    private Long categoryId;

    private String coverImage;

    @TableField(exist = false)
    private List<Chapter> chapters;

    private Integer status;

    private Integer isHot;

    private Integer isNew;

    private Integer isRecommend;

    private Integer duration;

    private String teacher;

    @TableField(exist = false)
    private Category category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
