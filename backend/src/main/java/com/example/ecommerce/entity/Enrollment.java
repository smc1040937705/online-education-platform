package com.example.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("enrollments")
public class Enrollment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String enrollmentNo;

    private Long userId;

    private Long courseId;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal payAmount;

    private Integer payType;

    private Integer status;

    private LocalDateTime payTime;

    private LocalDateTime expireTime;

    private LocalDateTime completeTime;

    private Integer progress;

    private String remark;

    @TableField(exist = false)
    private List<EnrollmentProgress> progressList;

    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Course course;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
