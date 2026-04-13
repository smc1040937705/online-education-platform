package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    @Update("UPDATE users SET balance = balance + #{amount} WHERE id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Update("UPDATE users SET balance = balance - #{amount} WHERE id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
