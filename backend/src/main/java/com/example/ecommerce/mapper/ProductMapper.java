package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM products WHERE status = 1 AND deleted = 0 ORDER BY sales DESC LIMIT #{limit}")
    List<Product> findHotProducts(@Param("limit") int limit);

    @Select("SELECT * FROM products WHERE status = 1 AND deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<Product> findNewProducts(@Param("limit") int limit);

    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 1 AND deleted = 0")
    Page<Product> findByCategory(Page<Product> page, @Param("categoryId") Long categoryId);

    @Update("UPDATE products SET stock = stock - #{quantity}, sales = sales + #{quantity} " +
            "WHERE id = #{productId} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE products SET stock = stock + #{quantity}, sales = sales - #{quantity} " +
            "WHERE id = #{productId}")
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
