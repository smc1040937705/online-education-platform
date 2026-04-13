package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private String detail;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Long categoryId;
    private String categoryName;
    private String mainImage;
    private List<String> subImages;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
    private LocalDateTime createdAt;
}
