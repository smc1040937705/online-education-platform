package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chapters")
public class Chapter {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String name;

    private Integer sortOrder;

    private Integer duration;

    private String videoUrl;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
