# 故障排查指南 (Troubleshooting Guide)

本指南帮助您诊断和解决考研学习小程序项目中的常见问题。

This guide helps you diagnose and resolve common issues in the Postgraduate Study Mini Program project.

## 目录 (Table of Contents)

- [启动问题](#启动问题)
- [数据库问题](#数据库问题)
- [认证问题](#认证问题)
- [Redis问题](#redis问题)
- [文件上传问题](#文件上传问题)
- [性能问题](#性能问题)
- [Docker问题](#docker问题)

## 启动问题 (Startup Issues)

### 应用无法启动

#### 症状
```
Error starting ApplicationContext
```

#### 可能原因和解决方案

**1. 端口被占用**

```bash
# 检查端口占用
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Linux/Mac

# 解决方案1: 杀死占用进程
kill -9 <PID>

# 解决方案2: 修改端口
# application.yml
server:
  port: 9090
```

**2. 配置文件错误**

```bash
# 检查YAML格式
# 确保缩进正确（使用2个空格）
# 确保没有制表符

# 验证配置
mvn spring-boot:run -Dspring-boot.run.arguments=--debug
```

**3. 依赖冲突**

```bash
# 查看依赖树
mvn dependency:tree

# 排除冲突依赖
<dependency>
    <groupId>...</groupId>
    <artifactId>...</artifactId>
    <exclusions>
        <exclusion>
            <groupId>conflict-group</groupId>
            <artifactId>conflict-artifact</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Bean创建失败

#### 症状
```
Error creating bean with name 'xxx'
```

#### 解决方案

```bash
# 检查组件扫描
@SpringBootApplication
@ComponentScan(basePackages = "com.studyapp")

# 检查依赖注入
# 确保使用了正确的注解：@Service, @Repository, @Component

# 检查循环依赖
# 使用构造器注入或@Lazy注解
```

## 数据库问题 (Database Issues)

### 无法连接数据库

#### 症状
```
Communications link failure
java.sql.SQLException: Access denied for user
```

#### 诊断步骤

```bash
# 1. 检查MySQL是否运行
# Windows
net start mysql

# Linux
sudo systemctl status mysql

# Docker
docker ps | grep mysql

# 2. 测试连接
mysql -h localhost -u root -p

# 3. 检查配置
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyapp?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

# 4. 检查防火墙
# Linux
sudo ufw status
sudo ufw allow 3306

# 5. 检查MySQL绑定地址
# /etc/mysql/my.cnf
bind-address = 0.0.0.0  # 允许远程连接
```

### 表不存在

#### 症状
```
Table 'studyapp.user' doesn't exist
```

#### 解决方案

```bash
# 1. 检查数据库是否创建
CREATE DATABASE IF NOT EXISTS studyapp 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 检查JPA配置
spring:
  jpa:
    hibernate:
      ddl-auto: update  # 开发环境使用update

# 3. 手动初始化表（如果需要）
# 查看src/main/resources/schema.sql

# 4. 清空并重建
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # 谨慎使用，会删除数据！
```

### 字符编码问题

#### 症状
```
中文显示为乱码
```

#### 解决方案

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyapp?useUnicode=true&characterEncoding=utf8&useSSL=false

# MySQL配置
# /etc/mysql/my.cnf
[client]
default-character-set=utf8mb4

[mysql]
default-character-set=utf8mb4

[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

# 重启MySQL
sudo systemctl restart mysql
```

## 认证问题 (Authentication Issues)

### JWT Token无效

#### 症状
```
401 Unauthorized
Invalid JWT token
```

#### 诊断步骤

```bash
# 1. 检查token格式
# 应该是: Bearer <token>

# 2. 检查token是否过期
# 解码JWT: https://jwt.io

# 3. 检查密钥配置
jwt:
  secret: ${JWT_SECRET}  # 确保环境变量已设置

# 4. 检查请求头
Authorization: Bearer eyJhbGc...

# 5. 调试JWT过滤器
# JwtAuthenticationFilter.java
logger.debug("Processing token: {}", token);
```

### 微信登录失败

#### 症状
```
WeChat login failed
Invalid code
```

#### 解决方案

```bash
# 1. 检查AppID和Secret
wechat:
  miniapp:
    appid: your_real_appid
    secret: your_real_secret

# 2. 检查code是否过期
# 微信code只能使用一次，5分钟内有效

# 3. 检查网络连接
# 测试微信API
curl "https://api.weixin.qq.com/sns/jscode2session?appid=xxx&secret=xxx&js_code=xxx&grant_type=authorization_code"

# 4. 查看日志
# UserService.java中的登录日志
```

## Redis问题 (Redis Issues)

### Redis连接失败

#### 症状
```
Unable to connect to Redis
Connection refused
```

#### 解决方案

```bash
# 1. 检查Redis是否运行
redis-cli ping

# 2. 启动Redis
# Windows: redis-server.exe
# Linux: redis-server
# Docker: docker-compose up -d redis

# 3. 检查连接配置
spring:
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码

# 4. 测试连接
redis-cli -h localhost -p 6379

# 5. 检查防火墙
sudo ufw allow 6379
```

### 缓存不生效

#### 症状
```
数据每次都从数据库查询
```

#### 诊断步骤

```bash
# 1. 检查Redis配置是否启用
# RedisConfig.java应该加载

# 2. 检查缓存注解
@Cacheable(value = "users", key = "#userId")

# 3. 查看Redis中的数据
redis-cli
> KEYS *
> GET user:1

# 4. 检查日志
logging:
  level:
    org.springframework.cache: DEBUG

# 5. 清空缓存测试
redis-cli FLUSHDB
```

## 文件上传问题 (File Upload Issues)

### 文件上传失败

#### 症状
```
Maximum upload size exceeded
File upload failed
```

#### 解决方案

```yaml
# 1. 增加文件大小限制
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

# 2. 检查文件权限
# Linux
chmod 755 /uploads
chown studyapp:studyapp /uploads

# 3. 检查磁盘空间
df -h

# 4. 检查COS配置（如果使用）
tencent:
  cos:
    secret-id: ${COS_SECRET_ID}
    secret-key: ${COS_SECRET_KEY}
    bucket-name: your-bucket
```

### 无法访问上传的文件

#### 症状
```
404 Not Found
Access Denied
```

#### 解决方案

```java
// 1. 配置静态资源映射
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/uploads/");
    }
}

// 2. 检查Security配置
http.authorizeRequests()
    .antMatchers("/uploads/**").permitAll()

// 3. 使用COS的CDN URL
```

## 性能问题 (Performance Issues)

### 应用响应慢

#### 诊断步骤

```bash
# 1. 查看JVM内存使用
jstat -gc <pid> 1000

# 2. 生成堆转储
jmap -dump:live,format=b,file=heap.bin <pid>

# 3. 分析慢查询
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

# 4. 使用Actuator监控
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used

# 5. 启用慢查询日志（MySQL）
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

### 内存溢出

#### 症状
```
java.lang.OutOfMemoryError: Java heap space
```

#### 解决方案

```bash
# 1. 增加堆内存
java -Xms512m -Xmx2048m -jar app.jar

# 2. Docker环境
environment:
  JAVA_OPTS: "-Xms1024m -Xmx2048m"

# 3. 分析内存泄漏
# 使用 VisualVM 或 MAT 分析堆转储

# 4. 优化代码
# - 使用分页查询
# - 及时关闭资源
# - 避免大对象
```

## Docker问题 (Docker Issues)

### 容器无法启动

#### 症状
```
Error response from daemon
Container exited with code 1
```

#### 诊断步骤

```bash
# 1. 查看容器日志
docker logs studyapp-backend

# 2. 查看容器状态
docker ps -a

# 3. 进入容器调试
docker exec -it studyapp-backend sh

# 4. 检查健康检查
docker inspect studyapp-backend | grep -A 10 Health

# 5. 重建容器
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### 容器间网络问题

#### 症状
```
Unable to connect to MySQL from app container
```

#### 解决方案

```bash
# 1. 检查网络
docker network ls
docker network inspect studyapp_studyapp-network

# 2. 使用服务名而非localhost
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/studyapp

# 3. 检查depends_on
services:
  app:
    depends_on:
      mysql:
        condition: service_healthy

# 4. 测试连接
docker exec -it studyapp-backend ping mysql
```

## 常用诊断命令 (Common Diagnostic Commands)

### 应用层

```bash
# 查看应用日志
tail -f logs/app.log

# 查看JVM信息
jinfo <pid>

# 线程转储
jstack <pid> > thread-dump.txt

# 查看Spring环境
curl http://localhost:8080/api/actuator/env
```

### 系统层

```bash
# 查看系统资源
top
htop
free -h
df -h

# 查看网络连接
netstat -tupln
ss -tupln

# 查看端口监听
lsof -i :8080
```

### Docker

```bash
# 查看所有容器
docker ps -a

# 查看容器资源使用
docker stats

# 清理未使用资源
docker system prune -a

# 查看镜像
docker images
```

## 获取帮助 (Getting Help)

如果以上方法都无法解决问题：

1. **查看完整日志**: 包含错误堆栈信息
2. **收集环境信息**: 操作系统、Java版本、MySQL版本等
3. **复现步骤**: 详细描述如何触发问题
4. **提交Issue**: 在GitHub上创建Issue，包含以上信息

### 日志收集脚本

```bash
#!/bin/bash
# collect-logs.sh

echo "=== System Information ==="
uname -a
java -version

echo "=== Application Logs ==="
tail -n 100 logs/app.log

echo "=== Docker Containers ==="
docker ps -a

echo "=== Docker Logs ==="
docker-compose logs --tail=50

echo "=== Resource Usage ==="
free -h
df -h

echo "=== Network ==="
netstat -tupln | grep -E '8080|3306|6379'
```

## 预防性维护 (Preventive Maintenance)

```bash
# 定期清理日志
find /var/log -name "*.log" -mtime +30 -delete

# 定期备份数据库
mysqldump -u root -p studyapp > backup_$(date +%Y%m%d).sql

# 监控磁盘空间
df -h | awk '$5 > 80 {print $0}'

# 更新依赖
mvn versions:display-dependency-updates

# Docker资源清理
docker system prune -a --volumes
```

---

**提示**: 遇到问题时保持冷静，系统性地排查，通常能快速定位和解决问题。
