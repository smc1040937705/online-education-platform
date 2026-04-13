package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM carts WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_at DESC")
    List<Cart> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM carts WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 0")
    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT * FROM carts WHERE id IN (${ids}) AND user_id = #{userId} AND deleted = 0")
    List<Cart> findByIdsAndUserId(@Param("ids") String ids, @Param("userId") Long userId);
}
