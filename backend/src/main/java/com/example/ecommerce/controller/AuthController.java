package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return ApiResponse.success(result);
    }

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = userService.register(request);
        return ApiResponse.success(user);
    }
}
