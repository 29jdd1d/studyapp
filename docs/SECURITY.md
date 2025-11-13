# 安全最佳实践 (Security Best Practices)

本文档描述了考研学习小程序项目的安全最佳实践和安全配置指南。

## 目录 (Table of Contents)

- [配置安全](#配置安全)
- [认证与授权](#认证与授权)
- [数据保护](#数据保护)
- [API安全](#api安全)
- [部署安全](#部署安全)
- [安全检查清单](#安全检查清单)

## 配置安全 (Configuration Security)

### 1. 环境变量

**永远不要**将敏感信息硬编码在代码中。使用环境变量或外部配置文件。

```yaml
# ❌ 错误示例
jwt:
  secret: "my_secret_key"

# ✅ 正确示例
jwt:
  secret: ${JWT_SECRET}
```

### 2. 生产环境配置

创建 `application-prod.yml`:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  jpa:
    show-sql: false  # 生产环境关闭SQL日志
    hibernate:
      ddl-auto: validate  # 不自动修改表结构

jwt:
  secret: ${JWT_SECRET}  # 至少32位随机字符串
  expiration: 3600000  # 1小时（根据需求调整）

# 禁用Swagger
springfox:
  documentation:
    enabled: false
```

### 3. 敏感信息加密

使用 Jasypt 加密配置文件中的敏感信息：

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>
```

```yaml
spring:
  datasource:
    password: ENC(encrypted_password_here)
```

## 认证与授权 (Authentication & Authorization)

### 1. JWT 令牌安全

#### 密钥管理

```bash
# 生成强随机密钥（至少256位）
openssl rand -base64 32
```

#### JWT 配置建议

```yaml
jwt:
  secret: ${JWT_SECRET}  # 至少32字节
  expiration: 3600000    # 1小时（不要设置太长）
  refresh-expiration: 604800000  # 7天
```

#### JWT 实现要点

```java
// ✅ 添加过期时间
.setExpiration(new Date(System.currentTimeMillis() + expiration))

// ✅ 添加签发者
.setIssuer("studyapp")

// ✅ 添加受众
.setAudience("studyapp-users")

// ✅ 使用强签名算法
.signWith(SignatureAlgorithm.HS512, secret)
```

### 2. 密码安全

#### 密码策略

```java
// 使用BCrypt加密密码
@Autowired
private BCryptPasswordEncoder passwordEncoder;

public void createUser(UserDTO dto) {
    String encodedPassword = passwordEncoder.encode(dto.getPassword());
    // 保存encodedPassword
}

// 验证密码
public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
```

#### 密码要求

- 最小长度: 8位
- 包含: 大写字母、小写字母、数字、特殊字符
- 定期更换（可选）
- 不允许常见密码

```java
@Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "密码必须至少8位，包含大小写字母、数字和特殊字符"
)
private String password;
```

### 3. 会话管理

```java
// 配置会话超时
server:
  servlet:
    session:
      timeout: 30m  # 30分钟

// 启用CSRF保护（API可以禁用）
http.csrf().disable();  // 仅在使用JWT时

// 配置CORS
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://your-domain.com")  // 不要使用 "*"
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

## 数据保护 (Data Protection)

### 1. 输入验证

**始终**验证用户输入：

```java
@PostMapping("/user")
public Result<User> createUser(@Valid @RequestBody UserDTO dto) {
    // @Valid 自动验证
}

// DTO中添加验证注解
public class UserDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
```

### 2. SQL注入防护

```java
// ✅ 使用JPA或MyBatis的参数化查询
@Query("SELECT u FROM User u WHERE u.username = :username")
User findByUsername(@Param("username") String username);

// ❌ 避免字符串拼接SQL
// String sql = "SELECT * FROM user WHERE username = '" + username + "'";
```

### 3. XSS防护

```java
// 对输出进行HTML转义
import org.springframework.web.util.HtmlUtils;

public String sanitize(String input) {
    return HtmlUtils.htmlEscape(input);
}

// 或使用OWASP Java HTML Sanitizer
<dependency>
    <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
    <artifactId>owasp-java-html-sanitizer</artifactId>
    <version>20220608.1</version>
</dependency>
```

### 4. 敏感数据保护

```java
// 不要在日志中输出敏感信息
logger.info("User login: {}", user.getUsername());  // ✅
logger.info("User login: {}", user.getPassword());  // ❌

// DTO中隐藏敏感字段
@JsonIgnore
private String password;

@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String secretKey;
```

## API安全 (API Security)

### 1. 速率限制

使用 Bucket4j 或 Spring Cloud Gateway 实现速率限制：

```java
// 添加依赖
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>

// 实现速率限制
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String key = getClientIP(request);
        Bucket bucket = resolveBucket(key);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);  // Too Many Requests
        }
    }
}
```

### 2. HTTPS强制

```yaml
# 强制使用HTTPS
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
    key-store-type: PKCS12
```

Nginx配置：

```nginx
server {
    listen 80;
    return 301 https://$host$request_uri;
}
```

### 3. 请求大小限制

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  max-http-header-size: 16KB
  tomcat:
    max-swallow-size: 2MB
```

### 4. 安全响应头

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .headers()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .xssProtection()
                .and()
                .frameOptions().deny()
                .and()
                .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000);
    }
}
```

## 部署安全 (Deployment Security)

### 1. Docker安全

```dockerfile
# 使用非root用户
RUN groupadd -r studyapp && useradd -r -g studyapp studyapp
USER studyapp

# 最小化镜像
FROM openjdk:8-jre-slim  # 使用slim版本

# 扫描漏洞
docker scan studyapp-backend:latest
```

### 2. 数据库安全

```sql
-- 创建专用数据库用户
CREATE USER 'studyapp'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON studyapp.* TO 'studyapp'@'localhost';
FLUSH PRIVILEGES;

-- 不要使用root用户
```

### 3. 网络安全

```yaml
# docker-compose.yml
services:
  mysql:
    networks:
      - backend  # 不暴露到外部网络
  
  app:
    networks:
      - backend
      - frontend

networks:
  backend:
    internal: true  # 内部网络
  frontend:
    driver: bridge
```

### 4. 日志安全

```yaml
# 记录安全事件
logging:
  level:
    org.springframework.security: INFO
    com.studyapp.security: DEBUG
  
  file:
    name: /var/log/studyapp/app.log
    max-size: 10MB
    max-history: 30

# 不记录敏感信息
spring:
  jpa:
    show-sql: false  # 生产环境关闭
```

## 安全检查清单 (Security Checklist)

### 开发阶段

- [ ] 所有密码使用BCrypt加密
- [ ] 所有用户输入都进行验证
- [ ] 使用参数化查询，防止SQL注入
- [ ] 敏感数据不记录到日志
- [ ] API都需要认证授权
- [ ] 实现全局异常处理
- [ ] 添加输入长度限制
- [ ] XSS防护已启用

### 测试阶段

- [ ] 进行安全测试（OWASP Top 10）
- [ ] 测试认证绕过
- [ ] 测试权限提升
- [ ] 测试SQL注入
- [ ] 测试XSS攻击
- [ ] 压力测试
- [ ] 依赖漏洞扫描

### 部署阶段

- [ ] 使用环境变量存储敏感信息
- [ ] JWT密钥已更换为强随机值
- [ ] 数据库使用专用用户，非root
- [ ] 启用HTTPS
- [ ] 配置防火墙规则
- [ ] 设置速率限制
- [ ] 关闭不必要的端口
- [ ] Swagger文档在生产环境关闭
- [ ] 日志不包含敏感信息
- [ ] 定期备份数据
- [ ] 监控异常登录行为

### 运维阶段

- [ ] 定期更新依赖
- [ ] 定期备份数据库
- [ ] 监控安全日志
- [ ] 定期审查访问权限
- [ ] 定期进行安全审计
- [ ] 制定安全事件响应计划

## 安全工具推荐 (Security Tools)

### 1. 依赖检查

```bash
# Maven dependency check
mvn org.owasp:dependency-check-maven:check

# Snyk扫描
snyk test
```

### 2. 代码扫描

```bash
# SonarQube
mvn sonar:sonar

# SpotBugs
mvn spotbugs:check
```

### 3. 容器扫描

```bash
# Docker扫描
docker scan studyapp-backend:latest

# Trivy扫描
trivy image studyapp-backend:latest
```

## 应急响应 (Incident Response)

### 发现安全漏洞时

1. 立即评估影响范围
2. 隔离受影响的系统
3. 收集日志和证据
4. 修复漏洞
5. 通知相关人员
6. 编写事后报告

### 数据泄露处理

1. 确认泄露范围
2. 通知受影响用户
3. 重置相关凭证
4. 加强监控
5. 审查安全策略

## 参考资源 (References)

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security文档](https://docs.spring.io/spring-security/reference/index.html)
- [JWT最佳实践](https://tools.ietf.org/html/rfc8725)
- [CWE Top 25](https://cwe.mitre.org/top25/)

---

**重要提示**: 安全是一个持续的过程，需要定期审查和更新安全措施。
