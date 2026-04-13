package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateEnrollmentRequest {

    private Long courseId;
    private Integer payType;
    private String remark;
}
