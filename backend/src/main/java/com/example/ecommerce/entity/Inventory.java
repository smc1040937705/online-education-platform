package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventories")
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Integer stock;

    private Integer lockedStock;

    private Integer availableStock;

    private Integer warningStock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
