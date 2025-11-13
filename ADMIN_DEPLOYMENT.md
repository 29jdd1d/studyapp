# Vue3 管理后台部署指南

## 概述

本文档介绍如何部署Vue3管理后台到生产环境。

## 部署方式

### 方式一：使用 Nginx 部署

#### 1. 构建项目

```bash
cd admin-frontend
npm install
npm run build
```

构建完成后，会在 `dist` 目录生成静态文件。

#### 2. 配置 Nginx

创建 Nginx 配置文件 `/etc/nginx/conf.d/admin.conf`:

```nginx
server {
    listen 80;
    server_name admin.yourdomain.com;

    root /var/www/admin-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 启用 gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

#### 3. 上传文件

将 `dist` 目录上传到服务器:

```bash
scp -r dist/* user@server:/var/www/admin-frontend/dist/
```

#### 4. 重启 Nginx

```bash
sudo nginx -t
sudo systemctl restart nginx
```

### 方式二：使用 Docker 部署

#### 1. 创建 Dockerfile

在 `admin-frontend` 目录创建 `Dockerfile`:

```dockerfile
# Build stage
FROM node:20-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 2. 创建 nginx.conf

在 `admin-frontend` 目录创建 `nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
}
```

#### 3. 构建镜像

```bash
cd admin-frontend
docker build -t studyapp-admin:latest .
```

#### 4. 运行容器

```bash
docker run -d -p 80:80 --name studyapp-admin studyapp-admin:latest
```

### 方式三：与后端集成部署

#### 1. 构建前端

```bash
cd admin-frontend
npm install
npm run build
```

#### 2. 复制到 Spring Boot 静态资源目录

```bash
cp -r dist/* ../src/main/resources/static/admin/
```

#### 3. 配置 Spring Boot

在 `application.yml` 中添加:

```yaml
spring:
  web:
    resources:
      static-locations: classpath:/static/
```

#### 4. 访问

启动 Spring Boot 应用后，访问:
```
http://localhost:8080/admin/
```

## 环境变量配置

### 开发环境 (.env.development)

```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 生产环境 (.env.production)

```
VITE_API_BASE_URL=/api
```

或使用完整URL:

```
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

## 性能优化建议

### 1. 启用 CDN

在 `vite.config.js` 中配置 CDN:

```javascript
export default defineConfig({
  build: {
    rollupOptions: {
      external: ['vue', 'element-plus'],
      output: {
        globals: {
          vue: 'Vue',
          'element-plus': 'ElementPlus'
        }
      }
    }
  }
})
```

在 `index.html` 中引入 CDN:

```html
<script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
<script src="https://unpkg.com/element-plus"></script>
```

### 2. 代码分割

使用动态导入进行路由懒加载（已实现）:

```javascript
{
  path: '/dashboard',
  component: () => import('../views/dashboard/index.vue')
}
```

### 3. 压缩

生产构建时会自动压缩，如需进一步优化:

```javascript
// vite.config.js
export default defineConfig({
  build: {
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  }
})
```

## 常见问题

### 1. 刷新页面 404

确保 Nginx 配置了 `try_files $uri $uri/ /index.html;`

### 2. API 跨域问题

方案一：使用 Nginx 代理（推荐）
方案二：后端配置 CORS

### 3. 登录状态丢失

检查浏览器是否禁用了 localStorage

## 安全建议

1. 使用 HTTPS
2. 设置合适的 CORS 策略
3. 实现 CSP (Content Security Policy)
4. 定期更新依赖包
5. 不要在前端代码中存储敏感信息

## 监控和日志

建议使用以下工具进行监控:

1. Google Analytics - 用户行为分析
2. Sentry - 错误追踪
3. Nginx 访问日志 - 服务器监控

## 维护

定期进行以下维护:

```bash
# 更新依赖
npm update

# 检查安全漏洞
npm audit

# 修复安全漏洞
npm audit fix
```

## 回滚

如需回滚到之前版本:

```bash
# 保留备份
cp -r dist dist.backup

# 回滚
cp -r dist.backup dist
```

## 支持

如遇到问题，请查看:
- [Vue3 官方文档](https://vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [Vite 文档](https://vitejs.dev/)
