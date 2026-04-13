package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT * FROM reviews WHERE product_id = #{productId} AND status = 1 AND deleted = 0 ORDER BY created_at DESC")
    Page<Review> findByProductId(Page<Review> page, @Param("productId") Long productId);

    @Select("SELECT * FROM reviews WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_at DESC")
    List<Review> findByUserId(@Param("userId") Long userId);

    @Select("SELECT AVG(rating) FROM reviews WHERE product_id = #{productId} AND status = 1 AND deleted = 0")
    Double getAverageRating(@Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM reviews WHERE product_id = #{productId} AND status = 1 AND deleted = 0")
    Long countByProductId(@Param("productId") Long productId);
}
