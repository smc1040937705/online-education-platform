package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartIds;

    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    private Long couponId;

    private String remark;
}
