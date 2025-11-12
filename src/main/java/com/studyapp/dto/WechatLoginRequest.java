package com.studyapp.dto;

import lombok.Data;

/**
 * 微信登录请求
 * WeChat Login Request
 */
@Data
public class WechatLoginRequest {
    
    private String code;
    private String nickName;
    private String avatarUrl;
    private String gender;
}
