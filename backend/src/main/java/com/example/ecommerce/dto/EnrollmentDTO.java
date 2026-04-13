package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnrollmentDTO {

    private Long id;
    private String enrollmentNo;
    private Long userId;
    private String username;
    private Long courseId;
    private String courseName;
    private String courseCover;
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
    private LocalDateTime createdAt;
}
