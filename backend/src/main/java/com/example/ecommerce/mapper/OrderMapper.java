package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND deleted = 0")
    Order findByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_at DESC")
    Page<Order> findByUserId(Page<Order> page, @Param("userId") Long userId);

    @Select("SELECT * FROM orders WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<Order> findByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId} AND status = #{status} AND deleted = 0")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
