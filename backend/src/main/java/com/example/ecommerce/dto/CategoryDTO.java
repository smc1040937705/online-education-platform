package com.example.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer level;
    private Integer sortOrder;
    private String icon;
    private Integer status;
    private List<CategoryDTO> children;
}
