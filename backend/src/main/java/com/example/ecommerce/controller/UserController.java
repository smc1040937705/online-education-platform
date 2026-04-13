package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserDTO user = userService.getUserById(userPrincipal.getId());
        return ApiResponse.success(user);
    }

    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @RequestBody UserDTO userDTO) {
        UserDTO user = userService.updateUser(userPrincipal.getId(), userDTO);
        return ApiResponse.success(user);
    }

    @PostMapping("/password")
    public ApiResponse<Boolean> updatePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @RequestBody Map<String, String> params) {
        boolean result = userService.updatePassword(userPrincipal.getId(), 
            params.get("oldPassword"), params.get("newPassword"));
        return ApiResponse.success(result);
    }

    @PostMapping("/recharge")
    public ApiResponse<Boolean> recharge(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestBody Map<String, BigDecimal> params) {
        boolean result = userService.recharge(userPrincipal.getId(), params.get("amount"));
        return ApiResponse.success(result);
    }
}
