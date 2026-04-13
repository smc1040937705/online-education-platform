package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.CourseDTO;
import com.example.ecommerce.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {

    PageResult<CourseDTO> getCoursePage(int page, int size, Long categoryId, String keyword);

    CourseDTO getCourseById(Long id);

    List<CourseDTO> getHotCourses(int limit);

    List<CourseDTO> getNewCourses(int limit);

    CourseDTO createCourse(CourseDTO courseDTO);

    CourseDTO updateCourse(Long id, CourseDTO courseDTO);

    boolean deleteCourse(Long id);

    boolean updateEnrollment(Long id, Integer quantity);
}
