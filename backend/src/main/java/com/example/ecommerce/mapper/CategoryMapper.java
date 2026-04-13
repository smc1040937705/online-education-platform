package com.example.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ecommerce.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort_order")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM categories WHERE status = 1 AND deleted = 0 ORDER BY level, sort_order")
    List<Category> findAllActive();
}
