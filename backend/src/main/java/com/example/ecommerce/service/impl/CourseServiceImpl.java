package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.CourseDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Course;
import com.example.ecommerce.mapper.CategoryMapper;
import com.example.ecommerce.mapper.CourseMapper;
import com.example.ecommerce.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<CourseDTO> getCoursePage(int page, int size, Long categoryId, String keyword) {
        Page<Course> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1);
        wrapper.eq(Course::getDeleted, 0);

        if (categoryId != null) {
            wrapper.eq(Course::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getName, keyword);
        }

        wrapper.orderByDesc(Course::getCreatedAt);
        Page<Course> coursePage = courseMapper.selectPage(pageParam, wrapper);

        List<CourseDTO> dtoList = coursePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(coursePage.getTotal(), page, size, dtoList);
    }

    @Override
    @Cacheable(value = "course", key = "#id")
    public CourseDTO getCourseById(Long id) {
        Course course = getById(id);
        if (course == null || course.getDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }
        return convertToDTO(course);
    }

    @Override
    @Cacheable(value = "hotCourses")
    public List<CourseDTO> getHotCourses(int limit) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1);
        wrapper.eq(Course::getDeleted, 0);
        wrapper.eq(Course::getIsHot, 1);
        wrapper.orderByDesc(Course::getStudents);
        wrapper.last("LIMIT " + limit);
        
        List<Course> courses = courseMapper.selectList(wrapper);
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "newCourses")
    public List<CourseDTO> getNewCourses(int limit) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1);
        wrapper.eq(Course::getDeleted, 0);
        wrapper.eq(Course::getIsNew, 1);
        wrapper.orderByDesc(Course::getCreatedAt);
        wrapper.last("LIMIT " + limit);
        
        List<Course> courses = courseMapper.selectList(wrapper);
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"hotCourses", "newCourses"}, allEntries = true)
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Category category = categoryMapper.selectById(courseDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setStudents(0);
        course.setStatus(1);

        save(course);
        return convertToDTO(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = "course", key = "#id")
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = getById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setDetail(courseDTO.getDetail());
        course.setPrice(courseDTO.getPrice());
        course.setOriginalPrice(courseDTO.getOriginalPrice());
        course.setEnrollment(courseDTO.getEnrollment());
        course.setCategoryId(courseDTO.getCategoryId());
        course.setCoverImage(courseDTO.getCoverImage());
        course.setIsHot(courseDTO.getIsHot());
        course.setIsNew(courseDTO.getIsNew());
        course.setIsRecommend(courseDTO.getIsRecommend());
        course.setDuration(courseDTO.getDuration());
        course.setTeacher(courseDTO.getTeacher());

        updateById(course);
        return convertToDTO(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = "course", key = "#id")
    public boolean deleteCourse(Long id) {
        Course course = getById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "course", key = "#id")
    public boolean updateEnrollment(Long id, Integer quantity) {
        Course course = getById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        course.setEnrollment(course.getEnrollment() + quantity);
        return updateById(course);
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);

        Category category = categoryMapper.selectById(course.getCategoryId());
        if (category != null) {
            dto.setCategoryName(category.getName());
        }

        return dto;
    }
}
