package com.example.ecommerce.service;

import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.UserDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.security.JwtTokenProvider;
import com.example.ecommerce.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(1);
        user.setRole(0);

        when(userMapper.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "testuser", 0)).thenReturn("jwt-token-123");

        String token = userService.login(request);

        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(userMapper).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtTokenProvider).generateToken(1L, "testuser", 0);
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> userService.login(request), "用户名或密码错误");
        verify(userMapper).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), anyInt());
    }

    @Test
    void login_WithWrongPassword_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(1);

        when(userMapper.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(request), "用户名或密码错误");
        verify(userMapper).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), anyInt());
    }

    @Test
    void login_WithDisabledUser_ShouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("disableduser");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("disableduser");
        user.setPassword("encodedPassword");
        user.setStatus(0);

        when(userMapper.findByUsername("disableduser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> userService.login(request), "账户已被禁用");
        verify(userMapper).findByUsername("disableduser");
        verify(jwtTokenProvider, never()).generateToken(anyLong(), anyString(), anyInt());
    }

    @Test
    void register_WithValidData_ShouldCreateUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setPhone("13800138000");

        when(userMapper.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedNewPassword");
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        UserDTO result = userService.register(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(0, result.getRole());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(userMapper).findByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_WithExistingUsername_ShouldThrowException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");

        when(userMapper.findByUsername("existinguser")).thenReturn(existingUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(request));
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper).findByUsername("existinguser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userMapper.selectById(1L)).thenReturn(user);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        existingUser.setEmail("old@example.com");
        existingUser.setPhone("13800000000");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@example.com");
        userDTO.setPhone("13900000000");
        userDTO.setAvatar("new-avatar.jpg");

        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserDTO result = userService.updateUser(1L, userDTO);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("13900000000", result.getPhone());
        assertEquals("new-avatar.jpg", result.getAvatar());
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@example.com");

        when(userMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(999L, userDTO));
        assertEquals("用户不存在", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void updatePassword_WithValidOldPassword_ShouldUpdatePassword() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("oldpassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        boolean result = userService.updatePassword(1L, "oldpassword", "newpassword");

        assertTrue(result);
        verify(passwordEncoder).matches("oldpassword", "encodedOldPassword");
        verify(passwordEncoder).encode("newpassword");
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void updatePassword_WithWrongOldPassword_ShouldThrowException() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedOldPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePassword(1L, "wrongpassword", "newpassword"));
        assertEquals("原密码错误", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void updatePassword_WithInvalidUserId_ShouldThrowException() {
        when(userMapper.selectById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePassword(999L, "oldpassword", "newpassword"));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void recharge_WithValidAmount_ShouldIncreaseBalance() {
        BigDecimal amount = new BigDecimal("100.00");

        when(userMapper.addBalance(1L, amount)).thenReturn(1);

        boolean result = userService.recharge(1L, amount);

        assertTrue(result);
        verify(userMapper).addBalance(1L, amount);
    }

    @Test
    void recharge_WithZeroAmount_ShouldThrowException() {
        BigDecimal amount = BigDecimal.ZERO;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.recharge(1L, amount));
        assertEquals("充值金额必须大于0", exception.getMessage());
        verify(userMapper, never()).addBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void recharge_WithNegativeAmount_ShouldThrowException() {
        BigDecimal amount = new BigDecimal("-50.00");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.recharge(1L, amount));
        assertEquals("充值金额必须大于0", exception.getMessage());
        verify(userMapper, never()).addBalance(anyLong(), any(BigDecimal.class));
    }

    @Test
    void recharge_WithInvalidUserId_ShouldReturnFalse() {
        BigDecimal amount = new BigDecimal("100.00");

        when(userMapper.addBalance(999L, amount)).thenReturn(0);

        boolean result = userService.recharge(999L, amount);

        assertFalse(result);
        verify(userMapper).addBalance(999L, amount);
    }
}
