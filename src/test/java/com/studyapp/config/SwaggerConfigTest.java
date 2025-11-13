package com.studyapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for SwaggerConfig to verify Springfox compatibility fix
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "springfox.documentation.enabled=true"
})
class SwaggerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoadsWithSwaggerEnabled() {
        // Verify that the application context loads successfully with Swagger enabled
        assertNotNull(applicationContext);
        
        // Verify that the Docket bean is created
        Docket docket = applicationContext.getBean(Docket.class);
        assertNotNull(docket, "Swagger Docket bean should be created");
    }
}
