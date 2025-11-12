package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.dto.LoginResponse;
import com.studyapp.dto.UserDTO;
import com.studyapp.dto.WechatLoginRequest;
import com.studyapp.service.UserService;
import com.studyapp.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 * User Controller
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("微信登录")
    @PostMapping("/wechat-login")
    public Result<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest request) {
        try {
            LoginResponse response = userService.wechatLogin(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        UserDTO userDTO = userService.getUserInfo(userId);
        return Result.success(userDTO);
    }
    
    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<UserDTO> updateUserInfo(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        Long userId = getUserIdFromRequest(request);
        UserDTO updated = userService.updateUserInfo(userId, userDTO);
        return Result.success(updated);
    }
    
    @ApiOperation("获取学习数据看板")
    @GetMapping("/dashboard")
    public Result<UserDTO> getStudyDashboard(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        UserDTO dashboard = userService.getStudyDashboard(userId);
        return Result.success(dashboard);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
