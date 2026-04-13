package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PageResult<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        PageResult<ProductDTO> result = productService.getProductPage(page, size, categoryId, keyword);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ApiResponse.success(product);
    }

    @GetMapping("/hot")
    public ApiResponse<List<ProductDTO>> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductDTO> products = productService.getHotProducts(limit);
        return ApiResponse.success(products);
    }

    @GetMapping("/new")
    public ApiResponse<List<ProductDTO>> getNewProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductDTO> products = productService.getNewProducts(limit);
        return ApiResponse.success(products);
    }

    @PostMapping
    public ApiResponse<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.createProduct(productDTO);
        return ApiResponse.success(product);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO product = productService.updateProduct(id, productDTO);
        return ApiResponse.success(product);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteProduct(@PathVariable Long id) {
        boolean result = productService.deleteProduct(id);
        return ApiResponse.success(result);
    }
}
