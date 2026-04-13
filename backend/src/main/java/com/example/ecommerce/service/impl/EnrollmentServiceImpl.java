package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.EnrollmentDTO;
import com.example.ecommerce.dto.CreateEnrollmentRequest;
import com.example.ecommerce.entity.Course;
import com.example.ecommerce.entity.Enrollment;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.CourseMapper;
import com.example.ecommerce.mapper.EnrollmentMapper;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public EnrollmentDTO createEnrollment(Long userId, CreateEnrollmentRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Course course = courseMapper.selectById(request.getCourseId());
        if (course == null || course.getDeleted() == 1) {
            throw new RuntimeException("课程不存在");
        }

        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getUserId, userId);
        wrapper.eq(Enrollment::getCourseId, request.getCourseId());
        wrapper.eq(Enrollment::getDeleted, 0);
        Enrollment existing = enrollmentMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("已经报名过该课程");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentNo(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        enrollment.setUserId(userId);
        enrollment.setCourseId(course.getId());
        enrollment.setTotalAmount(course.getPrice());
        enrollment.setDiscountAmount(course.getPrice().subtract(course.getPrice()));
        enrollment.setPayAmount(course.getPrice());
        enrollment.setPayType(request.getPayType() != null ? request.getPayType() : 0);
        enrollment.setStatus(0);
        enrollment.setProgress(0);
        enrollment.setRemark(request.getRemark());

        save(enrollment);

        course.setEnrollment(course.getEnrollment() + 1);
        courseMapper.updateById(course);

        return convertToDTO(enrollment);
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = getById(id);
        if (enrollment == null || enrollment.getDeleted() == 1) {
            throw new RuntimeException("报名记录不存在");
        }
        return convertToDTO(enrollment);
    }

    @Override
    public Page<EnrollmentDTO> getUserEnrollments(Long userId, int page, int size) {
        Page<Enrollment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getUserId, userId);
        wrapper.eq(Enrollment::getDeleted, 0);
        wrapper.orderByDesc(Enrollment::getCreatedAt);

        Page<Enrollment> enrollmentPage = enrollmentMapper.selectPage(pageParam, wrapper);
        Page<EnrollmentDTO> dtoPage = new Page<>(enrollmentPage.getCurrent(), enrollmentPage.getSize(), enrollmentPage.getTotal());
        dtoPage.setRecords(enrollmentPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStatus(Integer status) {
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStatus, status);
        wrapper.eq(Enrollment::getDeleted, 0);
        wrapper.orderByDesc(Enrollment::getCreatedAt);

        return list(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelEnrollment(Long id, Long userId) {
        Enrollment enrollment = getById(id);
        if (enrollment == null || enrollment.getDeleted() == 1) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!enrollment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        if (enrollment.getStatus() == 2) {
            throw new RuntimeException("已完成学习，无法取消");
        }

        enrollment.setStatus(4);
        updateById(enrollment);

        Course course = courseMapper.selectById(enrollment.getCourseId());
        if (course != null) {
            course.setEnrollment(Math.max(0, course.getEnrollment() - 1));
            courseMapper.updateById(course);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean payEnrollment(Long id, Long userId) {
        Enrollment enrollment = getById(id);
        if (enrollment == null || enrollment.getDeleted() == 1) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!enrollment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        if (enrollment.getStatus() != 0) {
            throw new RuntimeException("订单状态异常");
        }

        enrollment.setStatus(1);
        enrollment.setPayTime(LocalDateTime.now());
        updateById(enrollment);

        return true;
    }

    @Override
    @Transactional
    public boolean completeEnrollment(Long id, Long userId) {
        Enrollment enrollment = getById(id);
        if (enrollment == null || enrollment.getDeleted() == 1) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!enrollment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        if (enrollment.getStatus() != 1) {
            throw new RuntimeException("订单状态异常");
        }

        enrollment.setStatus(2);
        enrollment.setCompleteTime(LocalDateTime.now());
        enrollment.setProgress(100);
        updateById(enrollment);

        Course course = courseMapper.selectById(enrollment.getCourseId());
        if (course != null) {
            course.setStudents(course.getStudents() + 1);
            courseMapper.updateById(course);
        }

        return true;
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        BeanUtils.copyProperties(enrollment, dto);

        User user = userMapper.selectById(enrollment.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        }

        Course course = courseMapper.selectById(enrollment.getCourseId());
        if (course != null) {
            dto.setCourseName(course.getName());
            dto.setCourseCover(course.getCoverImage());
        }

        return dto;
    }
}
