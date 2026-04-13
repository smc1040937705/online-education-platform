package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryDTO>> getCategoryTree() {
        List<CategoryDTO> categories = categoryService.getCategoryTree();
        return ApiResponse.success(categories);
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDTO> getCategory(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ApiResponse.success(category);
    }

    @PostMapping
    public ApiResponse<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        return ApiResponse.success(category);
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO category = categoryService.updateCategory(id, categoryDTO);
        return ApiResponse.success(category);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteCategory(@PathVariable Long id) {
        boolean result = categoryService.deleteCategory(id);
        return ApiResponse.success(result);
    }
}
