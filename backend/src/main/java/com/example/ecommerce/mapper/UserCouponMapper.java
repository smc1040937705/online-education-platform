package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND status = #{status} AND deleted = 0")
    List<UserCoupon> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND coupon_id = #{couponId} AND deleted = 0")
    UserCoupon findByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
