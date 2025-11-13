package com.studyapp.controller;

import com.studyapp.dto.WechatLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security test for UserController
 * Verifies that the wechat-login endpoint is accessible without authentication
 * while other endpoints require authentication
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void wechatLoginEndpoint_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Arrange
        WechatLoginRequest loginRequest = new WechatLoginRequest();
        loginRequest.setCode("test_code");
        loginRequest.setNickName("Test User");
        loginRequest.setAvatarUrl("https://example.com/avatar.jpg");
        loginRequest.setGender("1");

        // Act
        MvcResult result = mockMvc.perform(post("/user/wechat-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        // Assert
        // The endpoint should be accessible (not return 403 Forbidden)
        // It may return 400 or 500 due to invalid WeChat code, but should not be 403
        assertNotEquals(403, result.getResponse().getStatus(), 
            "WeChat login endpoint should not return 403 Forbidden");
    }

    @Test
    void userInfoEndpoint_ShouldRequireAuthentication() throws Exception {
        // Act & Assert
        // Without authentication token, should return 403 Forbidden
        mockMvc.perform(get("/user/info")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void dashboardEndpoint_ShouldRequireAuthentication() throws Exception {
        // Act & Assert
        // Without authentication token, should return 403 Forbidden
        mockMvc.perform(get("/user/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
