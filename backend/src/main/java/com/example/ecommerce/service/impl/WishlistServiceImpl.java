package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.WishlistDTO;
import com.example.ecommerce.entity.Course;
import com.example.ecommerce.entity.Wishlist;
import com.example.ecommerce.mapper.CourseMapper;
import com.example.ecommerce.mapper.WishlistMapper;
import com.example.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl extends ServiceImpl<WishlistMapper, Wishlist> implements WishlistService {

    private final WishlistMapper wishlistMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public WishlistDTO addToWishlist(Long userId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || course.getDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }

        LambdaQueryWrapper<Wishlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Wishlist::getUserId, userId);
        wrapper.eq(Wishlist::getCourseId, courseId);
        wrapper.eq(Wishlist::getDeleted, 0);
        Wishlist existing = wishlistMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("课程已在心愿单中");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setCourseId(courseId);
        wishlist.setSelected(1);

        save(wishlist);
        return convertToDTO(wishlist);
    }

    @Override
    @Transactional
    public boolean removeFromWishlist(Long id, Long userId) {
        Wishlist wishlist = getById(id);
        if (wishlist == null || wishlist.getDeleted() == 1) {
            throw new RuntimeException("心愿单记录不存在");
        }
        if (!wishlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        return removeById(id);
    }

    @Override
    public List<WishlistDTO> getUserWishlist(Long userId) {
        LambdaQueryWrapper<Wishlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Wishlist::getUserId, userId);
        wrapper.eq(Wishlist::getDeleted, 0);
        wrapper.orderByDesc(Wishlist::getCreatedAt);

        return list(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        WishlistDTO dto = new WishlistDTO();
        BeanUtils.copyProperties(wishlist, dto);

        Course course = courseMapper.selectById(wishlist.getCourseId());
        if (course != null) {
            dto.setCourseName(course.getName());
            dto.setCourseCover(course.getCoverImage());
            dto.setPrice(course.getPrice().intValue());
        }

        return dto;
    }
}
