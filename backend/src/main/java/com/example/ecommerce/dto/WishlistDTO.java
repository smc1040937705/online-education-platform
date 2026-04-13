package com.example.ecommerce.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishlistDTO {

    private Long id;
    private Long userId;
    private Long courseId;
    private String courseName;
    private String courseCover;
    private Integer price;
    private Integer selected;
    private LocalDateTime createdAt;
}
