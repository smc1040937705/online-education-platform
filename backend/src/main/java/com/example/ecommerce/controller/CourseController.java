package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.dto.CourseDTO;
import com.example.ecommerce.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<PageResult<CourseDTO>> getCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        PageResult<CourseDTO> result = courseService.getCoursePage(page, size, categoryId, keyword);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseDTO> getCourse(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ApiResponse.success(course);
    }

    @GetMapping("/hot")
    public ApiResponse<List<CourseDTO>> getHotCourses(
            @RequestParam(defaultValue = "10") int limit) {
        List<CourseDTO> courses = courseService.getHotCourses(limit);
        return ApiResponse.success(courses);
    }

    @GetMapping("/new")
    public ApiResponse<List<CourseDTO>> getNewCourses(
            @RequestParam(defaultValue = "10") int limit) {
        List<CourseDTO> courses = courseService.getNewCourses(limit);
        return ApiResponse.success(courses);
    }

    @PostMapping
    public ApiResponse<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO course = courseService.createCourse(courseDTO);
        return ApiResponse.success(course);
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        CourseDTO course = courseService.updateCourse(id, courseDTO);
        return ApiResponse.success(course);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteCourse(@PathVariable Long id) {
        boolean result = courseService.deleteCourse(id);
        return ApiResponse.success(result);
    }
}
