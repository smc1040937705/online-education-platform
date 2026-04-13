package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {

    PageResult<ProductDTO> getProductPage(int page, int size, Long categoryId, String keyword);

    ProductDTO getProductById(Long id);

    List<ProductDTO> getHotProducts(int limit);

    List<ProductDTO> getNewProducts(int limit);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    boolean deleteProduct(Long id);

    boolean updateStock(Long id, Integer quantity);
}
