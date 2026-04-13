package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Integer role;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
