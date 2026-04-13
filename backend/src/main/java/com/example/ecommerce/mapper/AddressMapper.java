package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {

    @Select("SELECT * FROM addresses WHERE user_id = #{userId} AND deleted = 0 ORDER BY is_default DESC, created_at DESC")
    List<Address> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM addresses WHERE user_id = #{userId} AND is_default = 1 AND deleted = 0 LIMIT 1")
    Address findDefaultByUserId(@Param("userId") Long userId);

    @Update("UPDATE addresses SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefault(@Param("userId") Long userId);
}
