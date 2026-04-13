package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.security.JwtTokenProvider;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BadCredentialsException("账户已被禁用");
        }
        return jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        user.setRole(0);
        user.setBalance(BigDecimal.ZERO);
        user.setAvatar("");

        userMapper.insert(user);

        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());

        updateById(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean recharge(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        return userMapper.addBalance(id, amount) > 0;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
