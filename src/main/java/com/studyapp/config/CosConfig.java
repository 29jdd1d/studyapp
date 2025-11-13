package com.studyapp.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 * Tencent Cloud COS Configuration
 * 
 * This configuration is only active when COS properties are configured
 */
@Configuration
@ConditionalOnProperty(name = "tencent.cos.secret-id")
public class CosConfig {
    
    @Value("${tencent.cos.secret-id}")
    private String secretId;
    
    @Value("${tencent.cos.secret-key}")
    private String secretKey;
    
    @Value("${tencent.cos.region}")
    private String region;
    
    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        
        // 设置bucket的地域
        Region regionConfig = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionConfig);
        
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
