package com.example.ecommerce.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChapterDTO {

    private Long id;
    private Long courseId;
    private String name;
    private Integer sortOrder;
    private Integer duration;
    private String videoUrl;
    private Integer status;
    private LocalDateTime createdAt;
}
