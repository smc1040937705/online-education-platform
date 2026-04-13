package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory_logs")
public class InventoryLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Integer type;

    private Integer quantity;

    private Integer beforeStock;

    private Integer afterStock;

    private String remark;

    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
