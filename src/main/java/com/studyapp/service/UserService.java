package com.studyapp.service;

import com.alibaba.fastjson.JSONObject;
import com.studyapp.dto.LoginResponse;
import com.studyapp.dto.UserDTO;
import com.studyapp.dto.WechatLoginRequest;
import com.studyapp.entity.User;
import com.studyapp.repository.UserRepository;
import com.studyapp.util.JwtUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 * User Service
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${wechat.miniapp.appid}")
    private String appId;
    
    @Value("${wechat.miniapp.secret}")
    private String appSecret;
    
    /**
     * 微信登录
     */
    @Transactional
    public LoginResponse wechatLogin(WechatLoginRequest request) throws Exception {
        // 获取微信openId
        String openId = getOpenIdFromWechat(request.getCode());
        
        if (openId == null) {
            throw new RuntimeException("微信登录失败");
        }
        
        // 查找或创建用户
        User user = userRepository.findByOpenId(openId)
                .orElseGet(() -> createNewUser(openId, request));
        
        // 更新用户信息
        if (request.getNickName() != null) {
            user.setNickName(request.getNickName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        userRepository.save(user);
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenId());
        
        // 返回响应
        UserDTO userDTO = convertToDTO(user);
        return new LoginResponse(token, userDTO);
    }
    
    /**
     * 从微信服务器获取openId
     */
    private String getOpenIdFromWechat(String code) throws Exception {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            
            JSONObject jsonObject = JSONObject.parseObject(result);
            return jsonObject.getString("openid");
        }
    }
    
    /**
     * 创建新用户
     */
    private User createNewUser(String openId, WechatLoginRequest request) {
        User user = new User();
        user.setOpenId(openId);
        user.setNickName(request.getNickName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setGender(request.getGender());
        return user;
    }
    
    /**
     * 获取用户信息（使用Redis缓存）
     */
    @Cacheable(value = "user", key = "#userId")
    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return convertToDTO(user);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    @CachePut(value = "user", key = "#userId")
    public UserDTO updateUserInfo(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (userDTO.getNickName() != null) {
            user.setNickName(userDTO.getNickName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getTargetUniversity() != null) {
            user.setTargetUniversity(userDTO.getTargetUniversity());
        }
        if (userDTO.getTargetMajor() != null) {
            user.setTargetMajor(userDTO.getTargetMajor());
        }
        if (userDTO.getExamYear() != null) {
            user.setExamYear(userDTO.getExamYear());
        }
        
        userRepository.save(user);
        return convertToDTO(user);
    }
    
    /**
     * 获取学习数据看板
     */
    @Cacheable(value = "user", key = "#userId")
    public UserDTO getStudyDashboard(Long userId) {
        return getUserInfo(userId);
    }
    
    /**
     * 更新学习统计数据
     */
    @Transactional
    @CacheEvict(value = "user", key = "#userId")
    public void updateStudyStats(Long userId, Integer studyHours, Integer completedQuestions, Integer correctQuestions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (studyHours != null) {
            user.setStudyHours(user.getStudyHours() + studyHours);
        }
        if (completedQuestions != null) {
            user.setCompletedQuestions(user.getCompletedQuestions() + completedQuestions);
        }
        if (correctQuestions != null) {
            user.setCorrectQuestions(user.getCorrectQuestions() + correctQuestions);
        }
        
        userRepository.save(user);
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
