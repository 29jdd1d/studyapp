# Docker部署指南 (Docker Deployment Guide)

本指南介绍如何使用Docker和Docker Compose部署考研学习小程序后端服务。

## 前提条件 (Prerequisites)

### 必需软件
- Docker 20.10+ 
- Docker Compose 2.0+ (或 Docker Desktop)
- Git

### 系统要求
- 内存: 4GB+ (推荐8GB)
- CPU: 2核+
- 磁盘: 20GB+
- 操作系统: Linux/macOS/Windows with WSL2

## 快速开始 (Quick Start)

### 1. 克隆项目

```bash
git clone https://github.com/29jdd1d/studyapp.git
cd studyapp
```

### 2. 配置环境变量

创建 `.env` 文件（可选，用于覆盖默认配置）:

```bash
# WeChat Mini Program Configuration
WECHAT_APPID=your_wechat_appid_here
WECHAT_SECRET=your_wechat_secret_here

# Tencent COS Configuration (Optional)
COS_SECRET_ID=your_cos_secret_id
COS_SECRET_KEY=your_cos_secret_key
COS_REGION=ap-guangzhou
COS_BUCKET=your_bucket_name

# JWT Secret (Change in production!)
JWT_SECRET=your_production_jwt_secret_key_min_32_chars
```

### 3. 启动服务

```bash
# 启动所有服务 (MySQL + Redis + App)
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app
```

### 4. 验证部署

```bash
# 检查健康状态
curl http://localhost:8080/api/actuator/health

# 访问Swagger文档
# 浏览器打开: http://localhost:8080/swagger-ui/
```

## 服务管理 (Service Management)

### 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 只启动特定服务
docker-compose up -d mysql redis
docker-compose up -d app
```

### 停止服务

```bash
# 停止所有服务
docker-compose stop

# 停止特定服务
docker-compose stop app
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart app
```

### 删除服务

```bash
# 停止并删除所有容器
docker-compose down

# 停止并删除所有容器和卷（数据会丢失！）
docker-compose down -v
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 实时查看特定服务日志
docker-compose logs -f app

# 查看最近100行日志
docker-compose logs --tail=100 app
```

## 数据持久化 (Data Persistence)

Docker Compose配置了三个数据卷:

```yaml
volumes:
  mysql-data:       # MySQL数据库数据
  redis-data:       # Redis缓存数据
  app-uploads:      # 应用上传的文件
```

### 数据备份

```bash
# 备份MySQL数据
docker exec studyapp-mysql mysqldump -u root -proot studyapp > backup.sql

# 备份Redis数据
docker exec studyapp-redis redis-cli SAVE
docker cp studyapp-redis:/data/dump.rdb ./redis-backup.rdb

# 备份上传文件
docker run --rm -v studyapp_app-uploads:/uploads -v $(pwd):/backup alpine tar czf /backup/uploads-backup.tar.gz /uploads
```

### 数据恢复

```bash
# 恢复MySQL数据
docker exec -i studyapp-mysql mysql -u root -proot studyapp < backup.sql

# 恢复Redis数据
docker cp ./redis-backup.rdb studyapp-redis:/data/dump.rdb
docker-compose restart redis
```

## 生产环境部署 (Production Deployment)

### 1. 安全配置

修改 `docker-compose.yml` 中的敏感信息:

```yaml
environment:
  # 修改数据库密码
  MYSQL_ROOT_PASSWORD: your_strong_password
  MYSQL_PASSWORD: your_app_password
  
  # 修改JWT密钥
  JWT_SECRET: your_very_long_and_random_secret_key_at_least_32_characters
```

### 2. 使用外部数据库（推荐）

如果已有MySQL和Redis服务器，修改 `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      # 使用外部数据库
      SPRING_DATASOURCE_URL: jdbc:mysql://your-mysql-host:3306/studyapp
      SPRING_DATASOURCE_USERNAME: your_username
      SPRING_DATASOURCE_PASSWORD: your_password
      
      # 使用外部Redis
      SPRING_REDIS_HOST: your-redis-host
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: your_redis_password
    
    # 注释掉depends_on
    # depends_on:
    #   - mysql
    #   - redis

# 注释掉MySQL和Redis服务
# mysql: ...
# redis: ...
```

### 3. 反向代理配置（Nginx）

创建 `nginx.conf`:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # HTTPS重定向
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 4. 资源限制

修改 `docker-compose.yml` 添加资源限制:

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2048M
        reservations:
          cpus: '1'
          memory: 1024M
    environment:
      JAVA_OPTS: -Xms1024m -Xmx2048m
```

## 监控和维护 (Monitoring & Maintenance)

### 1. 查看资源使用情况

```bash
# 查看容器资源使用
docker stats

# 查看容器详细信息
docker inspect studyapp-backend
```

### 2. 健康检查

```bash
# 检查应用健康状态
curl http://localhost:8080/api/actuator/health

# 查看应用指标
curl http://localhost:8080/api/actuator/metrics

# 查看应用信息
curl http://localhost:8080/api/actuator/info
```

### 3. 日志管理

配置日志旋转（使用docker日志驱动）:

```yaml
services:
  app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 4. 性能优化

#### JVM调优

```yaml
environment:
  JAVA_OPTS: >-
    -XX:+UseG1GC
    -XX:MaxGCPauseMillis=200
    -XX:+PrintGCDetails
    -XX:+PrintGCDateStamps
    -Xloggc:/app/gc.log
```

#### MySQL调优

```yaml
mysql:
  command: >-
    --default-authentication-plugin=mysql_native_password
    --character-set-server=utf8mb4
    --collation-server=utf8mb4_unicode_ci
    --max_connections=500
    --innodb_buffer_pool_size=512M
```

#### Redis调优

```yaml
redis:
  command: >-
    redis-server
    --appendonly yes
    --maxmemory 256mb
    --maxmemory-policy allkeys-lru
```

## 故障排查 (Troubleshooting)

### 应用无法启动

```bash
# 检查容器状态
docker-compose ps

# 查看应用日志
docker-compose logs app

# 检查端口占用
sudo netstat -tlnp | grep 8080

# 重新构建镜像
docker-compose build --no-cache app
docker-compose up -d app
```

### 数据库连接失败

```bash
# 检查MySQL容器
docker-compose ps mysql

# 测试数据库连接
docker exec -it studyapp-mysql mysql -u root -proot

# 查看MySQL日志
docker-compose logs mysql

# 重启MySQL
docker-compose restart mysql
```

### Redis连接失败

```bash
# 检查Redis容器
docker-compose ps redis

# 测试Redis连接
docker exec -it studyapp-redis redis-cli ping

# 查看Redis日志
docker-compose logs redis
```

### 内存不足

```bash
# 检查内存使用
free -h
docker stats

# 清理未使用的Docker资源
docker system prune -a

# 增加应用内存限制
# 修改docker-compose.yml中的JAVA_OPTS
```

## 更新部署 (Update Deployment)

### 更新应用代码

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 重新构建镜像
docker-compose build app

# 3. 重启应用（保留数据）
docker-compose up -d app

# 4. 查看日志确认启动成功
docker-compose logs -f app
```

### 回滚到之前版本

```bash
# 1. 停止当前容器
docker-compose stop app

# 2. 切换到之前的代码版本
git checkout <previous-commit>

# 3. 重新构建和启动
docker-compose build app
docker-compose up -d app
```

## CI/CD集成 (CI/CD Integration)

项目已配置GitHub Actions工作流 (`.github/workflows/ci-cd.yml`):

- ✅ 自动构建和测试
- ✅ 代码质量检查
- ✅ Docker镜像构建
- 🔄 Docker镜像推送（需配置）

### 配置Docker Registry

在GitHub仓库设置中添加Secrets:

```
DOCKER_USERNAME: your_dockerhub_username
DOCKER_PASSWORD: your_dockerhub_password
```

然后取消注释 `.github/workflows/ci-cd.yml` 中的 `docker-push` job。

## 常见问题 (FAQ)

### Q: 如何修改默认端口？

A: 修改 `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "9090:8080"  # 使用9090端口
```

### Q: 如何查看MySQL数据库内容？

A: 使用MySQL客户端连接:

```bash
docker exec -it studyapp-mysql mysql -u root -proot studyapp
```

或使用图形化工具连接 `localhost:3306`

### Q: 如何备份整个环境？

A: 备份docker-compose.yml、.env文件和所有数据卷

### Q: 生产环境推荐配置？

A: 
- 至少4GB内存
- 2核CPU
- 使用外部数据库（RDS）
- 配置HTTPS
- 启用日志收集
- 配置自动备份

## 参考资源 (References)

- [Docker官方文档](https://docs.docker.com/)
- [Docker Compose文档](https://docs.docker.com/compose/)
- [Spring Boot Docker指南](https://spring.io/guides/gs/spring-boot-docker/)
- 项目README: [../README.md](../README.md)
- 常规部署指南: [DEPLOYMENT.md](DEPLOYMENT.md)

---

**注意**: 生产环境部署前请务必:
1. 修改所有默认密码
2. 配置HTTPS
3. 设置防火墙规则
4. 启用备份策略
5. 配置监控告警
