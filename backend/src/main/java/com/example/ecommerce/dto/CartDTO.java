package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private Integer selected;
    private Integer stock;
    private LocalDateTime createdAt;
}
