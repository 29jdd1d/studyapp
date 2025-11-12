package com.studyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 * Login Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private UserDTO user;
}
