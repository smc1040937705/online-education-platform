package com.example.ecommerce.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getStatus, 1)
        );

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }
}
