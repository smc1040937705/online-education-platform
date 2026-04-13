package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryMapper.findAllActive();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryTree() {
        List<Category> allCategories = categoryMapper.findAllActive();
        return buildTree(allCategories, 0L);
    }

    private List<CategoryDTO> buildTree(List<Category> categories, Long parentId) {
        List<CategoryDTO> result = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId().equals(parentId)) {
                CategoryDTO dto = convertToDTO(category);
                dto.setChildren(buildTree(categories, category.getId()));
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(1);

        if (category.getParentId() == null) {
            category.setParentId(0L);
            category.setLevel(1);
        } else {
            Category parent = getById(category.getParentId());
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
            category.setLevel(parent.getLevel() + 1);
        }

        save(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setIcon(categoryDTO.getIcon());
        category.setSortOrder(categoryDTO.getSortOrder());

        updateById(category);
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return removeById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
