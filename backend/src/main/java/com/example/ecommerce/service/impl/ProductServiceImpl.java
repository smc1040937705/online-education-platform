package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<ProductDTO> getProductPage(int page, int size, Long categoryId, String keyword) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.eq(Product::getDeleted, 0);

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }

        wrapper.orderByDesc(Product::getCreatedAt);
        Page<Product> productPage = productMapper.selectPage(pageParam, wrapper);

        List<ProductDTO> dtoList = productPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(productPage.getTotal(), page, size, dtoList);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = getById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new RuntimeException("商品不存在");
        }
        return convertToDTO(product);
    }

    @Override
    @Cacheable(value = "hotProducts")
    public List<ProductDTO> getHotProducts(int limit) {
        List<Product> products = productMapper.findHotProducts(limit);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "newProducts")
    public List<ProductDTO> getNewProducts(int limit) {
        List<Product> products = productMapper.findNewProducts(limit);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"hotProducts", "newProducts"}, allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        Category category = categoryMapper.selectById(productDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setSales(0);
        product.setStatus(1);

        save(product);
        return convertToDTO(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = getById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setDetail(productDTO.getDetail());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setStock(productDTO.getStock());
        product.setCategoryId(productDTO.getCategoryId());
        product.setMainImage(productDTO.getMainImage());
        product.setIsHot(productDTO.getIsHot());
        product.setIsNew(productDTO.getIsNew());
        product.setIsRecommend(productDTO.getIsRecommend());

        updateById(product);
        return convertToDTO(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public boolean deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public boolean updateStock(Long id, Integer quantity) {
        Product product = getById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStock(product.getStock() + quantity);
        return updateById(product);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            dto.setCategoryName(category.getName());
        }

        return dto;
    }
}
