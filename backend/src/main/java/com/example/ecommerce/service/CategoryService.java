package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getCategoryTree();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    boolean deleteCategory(Long id);
}
