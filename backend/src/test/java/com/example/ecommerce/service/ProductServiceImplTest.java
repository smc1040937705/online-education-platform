package com.example.ecommerce.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productService, "baseMapper", productMapper);
    }

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProductPage_WithNoFilters_ShouldReturnPagedProducts() {
        Page<Product> productPage = new Page<>(1, 10);
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("商品1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setCategoryId(1L);
        product1.setStatus(1);
        product1.setDeleted(0);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("商品2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setCategoryId(1L);
        product2.setStatus(1);
        product2.setDeleted(0);

        productPage.setRecords(Arrays.asList(product1, product2));
        productPage.setTotal(2);

        Category category = new Category();
        category.setId(1L);
        category.setName("测试分类");

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(productPage);
        when(categoryMapper.selectById(1L)).thenReturn(category);

        PageResult<ProductDTO> result = productService.getProductPage(1, 10, null, null);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getRecords().size());
        assertEquals("商品1", result.getRecords().get(0).getName());
        assertEquals("测试分类", result.getRecords().get(0).getCategoryName());
        verify(productMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getProductPage_WithCategoryFilter_ShouldFilterByCategory() {
        Page<Product> productPage = new Page<>(1, 10);
        Product product = new Product();
        product.setId(1L);
        product.setName("分类商品");
        product.setCategoryId(2L);
        product.setStatus(1);
        product.setDeleted(0);

        productPage.setRecords(Arrays.asList(product));
        productPage.setTotal(1);

        Category category = new Category();
        category.setId(2L);
        category.setName("指定分类");

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(productPage);
        when(categoryMapper.selectById(2L)).thenReturn(category);

        PageResult<ProductDTO> result = productService.getProductPage(1, 10, 2L, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("指定分类", result.getRecords().get(0).getCategoryName());
    }

    @Test
    void getProductPage_WithKeywordSearch_ShouldFilterByName() {
        Page<Product> productPage = new Page<>(1, 10);
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品名称");
        product.setCategoryId(1L);
        product.setStatus(1);
        product.setDeleted(0);

        productPage.setRecords(Arrays.asList(product));
        productPage.setTotal(1);

        Category category = new Category();
        category.setId(1L);
        category.setName("分类");

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(productPage);
        when(categoryMapper.selectById(1L)).thenReturn(category);

        PageResult<ProductDTO> result = productService.getProductPage(1, 10, null, "测试");

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("测试商品名称", result.getRecords().get(0).getName());
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategoryId(1L);
        product.setDeleted(0);

        Category category = new Category();
        category.setId(1L);
        category.setName("测试分类");

        when(productMapper.selectById(1L)).thenReturn(product);
        when(categoryMapper.selectById(1L)).thenReturn(category);

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试商品", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        assertEquals("测试分类", result.getCategoryName());
    }

    @Test
    void getProductById_WithDeletedProduct_ShouldThrowException() {
        Product product = new Product();
        product.setId(1L);
        product.setName("已删除商品");
        product.setDeleted(1);

        when(productMapper.selectById(1L)).thenReturn(product);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getProductById(1L));
        assertEquals("商品不存在", exception.getMessage());
    }

    @Test
    void getProductById_WithInvalidId_ShouldThrowException() {
        when(productMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getProductById(999L));
        assertEquals("商品不存在", exception.getMessage());
    }

    @Test
    void getHotProducts_ShouldReturnHotProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("热销商品1");
        product1.setCategoryId(1L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("热销商品2");
        product2.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("分类");

        when(productMapper.findHotProducts(10)).thenReturn(Arrays.asList(product1, product2));
        when(categoryMapper.selectById(1L)).thenReturn(category);

        List<ProductDTO> result = productService.getHotProducts(10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("热销商品1", result.get(0).getName());
        verify(productMapper).findHotProducts(10);
    }

    @Test
    void getNewProducts_ShouldReturnNewProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("新品1");
        product1.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("分类");

        when(productMapper.findNewProducts(5)).thenReturn(Arrays.asList(product1));
        when(categoryMapper.selectById(1L)).thenReturn(category);

        List<ProductDTO> result = productService.getNewProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("新品1", result.get(0).getName());
        verify(productMapper).findNewProducts(5);
    }

    @Test
    void createProduct_WithValidData_ShouldCreateProduct() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("新商品");
        productDTO.setPrice(new BigDecimal("199.99"));
        productDTO.setCategoryId(1L);
        productDTO.setStock(100);

        Category category = new Category();
        category.setId(1L);
        category.setName("分类");

        when(categoryMapper.selectById(1L)).thenReturn(category);
        when(productMapper.insert(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(1L);
            return 1;
        });

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("新商品", result.getName());
        assertEquals(0, result.getSales());
        assertEquals(1, result.getStatus());
        verify(categoryMapper).selectById(1L);
        verify(productMapper).insert(any(Product.class));
    }

    @Test
    void createProduct_WithInvalidCategory_ShouldThrowException() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("新商品");
        productDTO.setCategoryId(999L);

        when(categoryMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(productDTO));
        assertEquals("分类不存在", exception.getMessage());
        verify(productMapper, never()).insert(any(Product.class));
    }

    @Test
    void updateProduct_WithValidData_ShouldUpdateProduct() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("旧名称");
        existingProduct.setPrice(new BigDecimal("99.99"));
        existingProduct.setCategoryId(1L);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("新名称");
        productDTO.setDescription("新描述");
        productDTO.setPrice(new BigDecimal("199.99"));
        productDTO.setCategoryId(1L);
        productDTO.setStock(200);
        productDTO.setIsHot(1);

        when(productMapper.selectById(1L)).thenReturn(existingProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);
        when(categoryMapper.selectById(1L)).thenReturn(null);

        ProductDTO result = productService.updateProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("新名称", result.getName());
        assertEquals("新描述", result.getDescription());
        assertEquals(new BigDecimal("199.99"), result.getPrice());
        assertEquals(200, result.getStock());
        assertEquals(1, result.getIsHot());
        verify(productMapper).updateById(any(Product.class));
    }

    @Test
    void updateProduct_WithInvalidId_ShouldThrowException() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("新名称");

        when(productMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(999L, productDTO));
        assertEquals("商品不存在", exception.getMessage());
        verify(productMapper, never()).updateById(any(Product.class));
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");

        when(productMapper.selectById(1L)).thenReturn(product);

        boolean result = productService.deleteProduct(1L);

        assertTrue(result);
        verify(productMapper).deleteById(1L);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        when(productMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(999L));
        assertEquals("商品不存在", exception.getMessage());
        verify(productMapper, never()).deleteById(anyLong());
    }

    @Test
    void updateStock_WithValidData_ShouldUpdateStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setStock(100);

        when(productMapper.selectById(1L)).thenReturn(product);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        boolean result = productService.updateStock(1L, 50);

        assertTrue(result);
        assertEquals(150, product.getStock());
        verify(productMapper).updateById(any(Product.class));
    }

    @Test
    void updateStock_WithNegativeQuantity_ShouldDecreaseStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setStock(100);

        when(productMapper.selectById(1L)).thenReturn(product);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        boolean result = productService.updateStock(1L, -30);

        assertTrue(result);
        assertEquals(70, product.getStock());
    }

    @Test
    void updateStock_WithInvalidId_ShouldThrowException() {
        when(productMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateStock(999L, 10));
        assertEquals("商品不存在", exception.getMessage());
        verify(productMapper, never()).updateById(any(Product.class));
    }
}
