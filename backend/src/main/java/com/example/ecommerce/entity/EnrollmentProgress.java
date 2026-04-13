package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("enrollment_progress")
public class EnrollmentProgress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long enrollmentId;

    private Long chapterId;

    private Integer progress;

    private Integer status;

    private LocalDateTime lastWatchTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
