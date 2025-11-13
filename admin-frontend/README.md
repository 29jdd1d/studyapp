# 考研学习管理系统 - 后台管理前端

基于 Vue 3 + Vite + Element Plus 的后台管理系统。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **图标**: @element-plus/icons-vue

## 功能模块

### 1. 数据统计
- 系统概览
- 用户统计
- 资源统计
- 题库统计
- 社区统计

### 2. 用户管理
- 用户列表查询
- 用户信息编辑
- 用户状态管理
- 用户删除

### 3. 学习资源管理
- 资源列表
- 资源创建
- 资源编辑
- 资源发布/下架
- 资源删除

### 4. 题库管理
- 题目列表
- 题目创建
- 题目编辑
- 题目删除
- 支持多种题型（单选、多选、判断、简答）

### 5. 社区管理
- 帖子列表
- 帖子审核
- 帖子删除
- 内容管理

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

访问 http://localhost:5173

### 生产环境构建

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 配置说明

### 环境变量

在 `.env` 文件中配置：

```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 默认登录账号

- 用户名: admin
- 密码: admin123

## 目录结构

```
admin-frontend/
├── src/
│   ├── api/              # API 接口封装
│   │   ├── request.js    # Axios 请求配置
│   │   ├── auth.js       # 认证相关 API
│   │   └── admin.js      # 管理相关 API
│   ├── components/       # 组件
│   │   └── layout/       # 布局组件
│   ├── router/           # 路由配置
│   ├── views/            # 页面
│   │   ├── login/        # 登录页
│   │   ├── dashboard/    # 数据统计
│   │   ├── users/        # 用户管理
│   │   ├── resources/    # 资源管理
│   │   ├── questions/    # 题库管理
│   │   └── community/    # 社区管理
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── .env                  # 环境变量
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── index.html            # HTML 模板
├── package.json          # 项目配置
└── vite.config.js        # Vite 配置
```

## 注意事项

1. 请确保后端 API 服务已启动并运行在 http://localhost:8080
2. 首次登录使用默认账号密码
3. 所有 API 请求都会自动携带 JWT token（存储在 localStorage）
4. 登录过期后会自动跳转到登录页

## 与后端集成

后端需要提供以下 API 接口：

- POST `/api/admin/login` - 管理员登录
- GET `/api/admin/statistics` - 获取统计数据
- GET `/api/admin/users` - 获取用户列表
- GET `/api/admin/resources` - 获取资源列表
- GET `/api/admin/questions` - 获取题目列表
- GET `/api/admin/posts` - 获取帖子列表
- POST `/api/admin/posts/{id}/audit` - 审核帖子

更多 API 接口请参考主项目的 API 文档。

