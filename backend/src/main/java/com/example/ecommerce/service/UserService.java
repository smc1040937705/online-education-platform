package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.entity.User;

import java.math.BigDecimal;

public interface UserService extends IService<User> {

    String login(LoginRequest request);

    UserDTO register(RegisterRequest request);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    boolean updatePassword(Long id, String oldPassword, String newPassword);

    boolean recharge(Long id, BigDecimal amount);
}
