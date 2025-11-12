# 部署指南 (Deployment Guide)

## 环境要求

### 必需软件
- JDK 1.8 或更高版本
- Maven 3.6+
- MySQL 8.0+

### 推荐配置
- 内存: 2GB+
- CPU: 2核+
- 磁盘: 10GB+

## 快速部署步骤

### 1. 准备数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE studyapp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'studyapp'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON studyapp.* TO 'studyapp'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 配置应用

修改 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyapp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: studyapp  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码

wechat:
  miniapp:
    appid: your_wechat_appid  # 修改为你的微信小程序AppID
    secret: your_wechat_secret  # 修改为你的微信小程序Secret

jwt:
  secret: your_secure_jwt_secret_key  # 修改为更安全的密钥（建议32位以上）
  expiration: 86400000  # Token有效期（毫秒）
```

### 3. 编译项目

```bash
# 清理并编译
mvn clean compile

# 打包（跳过测试）
mvn package -DskipTests
```

编译完成后，会在 `target` 目录下生成 `postgraduate-study-1.0.0.jar`

### 4. 运行应用

#### 开发环境运行

```bash
# 使用Maven运行
mvn spring-boot:run

# 或直接运行jar包
java -jar target/postgraduate-study-1.0.0.jar
```

#### 生产环境运行

```bash
# 后台运行
nohup java -jar target/postgraduate-study-1.0.0.jar > app.log 2>&1 &

# 指定配置文件
java -jar target/postgraduate-study-1.0.0.jar --spring.config.location=file:./application-prod.yml

# 指定端口
java -jar target/postgraduate-study-1.0.0.jar --server.port=8080
```

### 5. 验证部署

访问以下URL验证部署是否成功：

- 应用健康检查: `http://localhost:8080/api/`
- Swagger文档: `http://localhost:8080/swagger-ui/`

## Docker 部署（推荐）

### 1. 创建 Dockerfile

在项目根目录创建 `Dockerfile`:

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/postgraduate-study-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 2. 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: studyapp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: studyapp
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password

  app:
    build: .
    container_name: studyapp
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/studyapp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root

volumes:
  mysql-data:
```

### 3. 运行 Docker Compose

```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止
docker-compose down
```

## Nginx 反向代理配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 系统监控

### 查看应用日志

```bash
# 实时查看日志
tail -f app.log

# 查看最后100行
tail -n 100 app.log

# 查找错误日志
grep "ERROR" app.log
```

### 查看进程状态

```bash
# 查找Java进程
ps aux | grep java

# 查看端口占用
netstat -tlnp | grep 8080
lsof -i :8080
```

### 停止应用

```bash
# 找到进程ID
ps aux | grep postgraduate-study

# 优雅停止
kill -15 <PID>

# 强制停止
kill -9 <PID>
```

## 常见问题

### 1. 端口被占用

```bash
# 查看端口占用
lsof -i :8080

# 修改端口
java -jar app.jar --server.port=8081
```

### 2. 数据库连接失败

检查：
- MySQL是否启动
- 数据库用户名密码是否正确
- 防火墙是否开放3306端口
- MySQL是否允许远程连接

### 3. 内存不足

```bash
# 限制JVM内存
java -Xmx512m -Xms256m -jar app.jar
```

### 4. 微信登录失败

检查：
- 微信小程序AppID和Secret是否正确
- 服务器是否能访问微信API
- 网络连接是否正常

## 性能优化

### JVM参数优化

```bash
java -server \
  -Xms1g -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -jar app.jar
```

### 数据库优化

```sql
-- 添加索引
CREATE INDEX idx_user_openid ON sys_user(open_id);
CREATE INDEX idx_resource_subject ON learning_resource(subject, published);
CREATE INDEX idx_question_subject ON question(subject, chapter);

-- 配置连接池
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## 备份与恢复

### 数据库备份

```bash
# 备份数据库
mysqldump -u root -p studyapp > studyapp_backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p studyapp < studyapp_backup_20240101.sql
```

### 定时备份脚本

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/backup/studyapp"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u root -pYOUR_PASSWORD studyapp > $BACKUP_DIR/db_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "db_*.sql" -mtime +7 -delete
```

添加到 crontab:
```bash
# 每天凌晨2点备份
0 2 * * * /path/to/backup.sh
```

## 安全建议

1. 修改默认的JWT密钥为更复杂的字符串
2. 使用HTTPS加密传输
3. 定期更新依赖包
4. 配置防火墙规则
5. 设置MySQL密码复杂度
6. 定期备份数据
7. 限制API访问频率

## 升级指南

1. 备份当前数据库
2. 停止当前应用
3. 部署新版本jar包
4. 检查配置文件变更
5. 启动新版本应用
6. 验证功能正常

## 技术支持

如遇到问题，请检查：
1. 应用日志: `app.log`
2. MySQL日志: `/var/log/mysql/error.log`
3. 系统资源使用情况: `top`, `free -h`, `df -h`
