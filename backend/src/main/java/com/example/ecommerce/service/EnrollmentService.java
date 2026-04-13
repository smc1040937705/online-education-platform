package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.EnrollmentDTO;
import com.example.ecommerce.dto.CreateEnrollmentRequest;
import com.example.ecommerce.dto.PageResult;
import com.example.ecommerce.entity.Enrollment;

import java.util.List;

public interface EnrollmentService extends IService<Enrollment> {

    EnrollmentDTO createEnrollment(Long userId, CreateEnrollmentRequest request);

    EnrollmentDTO getEnrollmentById(Long id);

    Page<EnrollmentDTO> getUserEnrollments(Long userId, int page, int size);

    List<EnrollmentDTO> getEnrollmentsByStatus(Integer status);

    boolean cancelEnrollment(Long id, Long userId);

    boolean payEnrollment(Long id, Long userId);

    boolean completeEnrollment(Long id, Long userId);
}
