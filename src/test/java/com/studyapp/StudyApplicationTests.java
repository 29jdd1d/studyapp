package com.studyapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用测试
 * Application Tests
 */
@SpringBootTest
@ActiveProfiles("test")
class StudyApplicationTests {
    
    @Test
    void contextLoads() {
        // 测试Spring上下文加载
        // Test Spring context loading
    }
}
