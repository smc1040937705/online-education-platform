package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Select("SELECT * FROM coupons WHERE status = 1 AND remain_count > 0 " +
            "AND start_time <= #{now} AND end_time >= #{now} AND deleted = 0")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    @Select("SELECT * FROM coupons WHERE code = #{code} AND deleted = 0")
    Coupon findByCode(@Param("code") String code);

    @Update("UPDATE coupons SET remain_count = remain_count - 1 " +
            "WHERE id = #{id} AND remain_count > 0")
    int deductCoupon(@Param("id") Long id);
}
