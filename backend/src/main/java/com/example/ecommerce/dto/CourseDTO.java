package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseDTO {

    private Long id;
    private String name;
    private String description;
    private String detail;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer enrollment;
    private Integer students;
    private Long categoryId;
    private String categoryName;
    private String coverImage;
    private List<ChapterDTO> chapters;
    private Integer status;
    private Integer isHot;
    private Integer isNew;
    private Integer isRecommend;
    private Integer duration;
    private String teacher;
    private LocalDateTime createdAt;
}
