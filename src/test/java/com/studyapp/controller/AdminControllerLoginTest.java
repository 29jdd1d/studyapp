package com.studyapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.dto.AdminLoginRequest;
import com.studyapp.entity.User;
import com.studyapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Admin login functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_ADMIN_USERNAME = "testadmin";
    private static final String TEST_ADMIN_PASSWORD = "admin123";
    private User testAdminUser;

    @BeforeEach
    void setUp() {
        // Clean up any existing test admin user
        userRepository.findByUsername(TEST_ADMIN_USERNAME).ifPresent(userRepository::delete);

        // Create a test admin user
        testAdminUser = new User();
        testAdminUser.setUsername(TEST_ADMIN_USERNAME);
        testAdminUser.setPassword(passwordEncoder.encode(TEST_ADMIN_PASSWORD));
        testAdminUser.setNickName("Test Admin");
        testAdminUser.setRole("ADMIN");
        testAdminUser.setEnabled(true);
        testAdminUser.setStudyDays(0);
        testAdminUser.setStudyHours(0);
        testAdminUser.setCompletedQuestions(0);
        testAdminUser.setCorrectQuestions(0);
        testAdminUser = userRepository.save(testAdminUser);
    }

    @Test
    void adminLoginEndpoint_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Arrange
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername(TEST_ADMIN_USERNAME);
        loginRequest.setPassword(TEST_ADMIN_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void adminLogin_WithValidCredentials_ShouldReturnTokenAndUserInfo() throws Exception {
        // Arrange
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername(TEST_ADMIN_USERNAME);
        loginRequest.setPassword(TEST_ADMIN_PASSWORD);

        // Act
        MvcResult result = mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value(notNullValue()))
                .andExpect(jsonPath("$.data.user.username").value(TEST_ADMIN_USERNAME))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("token"), "Response should contain a token");
    }

    @Test
    void adminLogin_WithInvalidUsername_ShouldReturnError() throws Exception {
        // Arrange
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("nonexistentuser");
        loginRequest.setPassword(TEST_ADMIN_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("登录失败")));
    }

    @Test
    void adminLogin_WithInvalidPassword_ShouldReturnError() throws Exception {
        // Arrange
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername(TEST_ADMIN_USERNAME);
        loginRequest.setPassword("wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("登录失败")));
    }

    @Test
    void adminLogin_WithDisabledAccount_ShouldReturnError() throws Exception {
        // Arrange - disable the admin account
        testAdminUser.setEnabled(false);
        userRepository.save(testAdminUser);

        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername(TEST_ADMIN_USERNAME);
        loginRequest.setPassword(TEST_ADMIN_PASSWORD);

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("登录失败")));
    }

    @Test
    void adminLogin_WithNonAdminUser_ShouldReturnError() throws Exception {
        // Arrange - create a regular user with username/password
        User regularUser = new User();
        regularUser.setUsername("regularuser");
        regularUser.setPassword(passwordEncoder.encode("password123"));
        regularUser.setNickName("Regular User");
        regularUser.setRole("USER");
        regularUser.setEnabled(true);
        regularUser.setStudyDays(0);
        regularUser.setStudyHours(0);
        regularUser.setCompletedQuestions(0);
        regularUser.setCorrectQuestions(0);
        userRepository.save(regularUser);

        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("regularuser");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("登录失败")));
    }

    @Test
    void adminLogout_ShouldReturnSuccess() throws Exception {
        // Act & Assert - logout doesn't require authentication as JWT is stateless
        mockMvc.perform(post("/admin/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void adminEndpoints_ShouldRequireAdminRole() throws Exception {
        // Arrange - login to get admin token
        AdminLoginRequest loginRequest = new AdminLoginRequest();
        loginRequest.setUsername(TEST_ADMIN_USERNAME);
        loginRequest.setPassword(TEST_ADMIN_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent)
                .get("data")
                .get("token")
                .asText();

        // Act & Assert - access admin endpoint with valid admin token
        mockMvc.perform(get("/admin/statistics")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoints_WithoutToken_ShouldReturnForbidden() throws Exception {
        // Act & Assert - access admin endpoint without token
        mockMvc.perform(get("/admin/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
