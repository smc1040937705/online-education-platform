package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("SELECT * FROM inventories WHERE product_id = #{productId} AND deleted = 0")
    Inventory findByProductId(@Param("productId") Long productId);

    @Update("UPDATE inventories SET stock = stock + #{quantity}, available_stock = available_stock + #{quantity} " +
            "WHERE product_id = #{productId}")
    int addStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE inventories SET stock = stock - #{quantity}, available_stock = available_stock - #{quantity} " +
            "WHERE product_id = #{productId} AND available_stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE inventories SET locked_stock = locked_stock + #{quantity}, " +
            "available_stock = available_stock - #{quantity} " +
            "WHERE product_id = #{productId} AND available_stock >= #{quantity}")
    int lockStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE inventories SET locked_stock = locked_stock - #{quantity}, " +
            "available_stock = available_stock + #{quantity} " +
            "WHERE product_id = #{productId} AND locked_stock >= #{quantity}")
    int unlockStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
