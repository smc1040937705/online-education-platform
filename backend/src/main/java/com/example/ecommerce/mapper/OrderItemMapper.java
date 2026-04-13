package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId} AND deleted = 0")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}
