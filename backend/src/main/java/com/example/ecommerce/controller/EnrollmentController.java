package com.example.ecommerce.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CreateEnrollmentRequest;
import com.example.ecommerce.dto.EnrollmentDTO;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    public ApiResponse<Page<EnrollmentDTO>> getUserEnrollments(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EnrollmentDTO> enrollments = enrollmentService.getUserEnrollments(userPrincipal.getId(), page, size);
        return ApiResponse.success(enrollments);
    }

    @GetMapping("/{id}")
    public ApiResponse<EnrollmentDTO> getEnrollment(@PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ApiResponse.success(enrollment);
    }

    @PostMapping
    public ApiResponse<EnrollmentDTO> createEnrollment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateEnrollmentRequest request) {
        EnrollmentDTO enrollment = enrollmentService.createEnrollment(userPrincipal.getId(), request);
        return ApiResponse.success(enrollment);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Boolean> cancelEnrollment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = enrollmentService.cancelEnrollment(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<Boolean> payEnrollment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = enrollmentService.payEnrollment(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Boolean> completeEnrollment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = enrollmentService.completeEnrollment(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<EnrollmentDTO>> getEnrollmentsByStatus(@PathVariable Integer status) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStatus(status);
        return ApiResponse.success(enrollments);
    }
}
