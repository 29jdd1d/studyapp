# 管理员登录功能文档 (Admin Login Documentation)

## 概述 (Overview)

本项目新增了管理员登录功能，允许管理员通过用户名和密码登录后台管理系统，而不仅仅依赖微信登录。

This project now includes an admin login feature that allows administrators to log in to the backend management system using username and password, rather than relying solely on WeChat login.

## API 端点 (API Endpoints)

### 1. 管理员登录 (Admin Login)

- **URL**: `POST /api/admin/login`
- **认证**: 无需认证
- **描述**: 管理员使用用户名和密码登录

**请求体 (Request Body)**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应 (Success Response)**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "nickName": "系统管理员",
      "role": "ADMIN",
      "enabled": true
    }
  }
}
```

**错误响应 (Error Response)**:
```json
{
  "code": 500,
  "message": "登录失败: 用户名或密码错误"
}
```

### 2. 管理员登出 (Admin Logout)

- **URL**: `POST /api/admin/logout`
- **认证**: 无需认证（JWT是无状态的）
- **描述**: 管理员登出系统

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功"
}
```

注意：由于使用JWT无状态认证，登出操作主要在前端完成（删除token），后端仅返回成功响应。

## 数据库变更 (Database Changes)

### User 实体新增字段 (New User Entity Fields)

在 `sys_user` 表中新增了两个字段：

- `username` (VARCHAR(50), UNIQUE): 管理员用户名
- `password` (VARCHAR(255)): BCrypt加密的密码

这些字段对于微信用户是可选的（NULL），但对于管理员账户是必需的。

## 初始化管理员账户 (Initialize Admin Account)

使用提供的SQL脚本创建默认管理员账户：

```bash
# 执行SQL脚本
mysql -u root -p studyapp < docs/admin-user-init.sql
```

**默认管理员账户**:
- 用户名: `admin`
- 密码: `admin123`
- 角色: `ADMIN`

**重要提示**: 请在生产环境中修改默认密码！

## 安全特性 (Security Features)

1. **密码加密**: 使用BCrypt加密存储密码
2. **角色验证**: 登录时验证用户角色必须为ADMIN
3. **账户状态检查**: 验证账户是否被禁用
4. **JWT令牌**: 包含用户ID、用户名和角色信息
5. **基于角色的访问控制**: 所有 `/admin/**` 端点（除登录/登出外）需要ADMIN角色

## 使用示例 (Usage Example)

### 1. 管理员登录

```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. 使用Token访问管理员端点

```bash
curl -X GET http://localhost:8080/api/admin/statistics \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 3. 管理员登出

```bash
curl -X POST http://localhost:8080/api/admin/logout \
  -H "Content-Type: application/json"
```

## 前端集成 (Frontend Integration)

前端已配置相应的API调用，位于 `admin-frontend/src/api/auth.js`:

```javascript
// 管理员登录
export function login(username, password) {
  return request({
    url: '/admin/login',
    method: 'post',
    data: { username, password }
  })
}

// 管理员登出
export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}
```

## 测试 (Testing)

项目包含9个全面的集成测试，覆盖以下场景：

1. 登录端点可在无认证情况下访问
2. 使用有效凭据登录成功
3. 无效用户名登录失败
4. 无效密码登录失败
5. 禁用账户登录失败
6. 非管理员用户登录失败
7. 登出功能正常工作
8. 管理员端点需要管理员角色
9. 无token访问管理员端点被拒绝

运行测试：
```bash
mvn test -Dtest=AdminControllerLoginTest
```

## 技术实现细节 (Technical Implementation)

### 关键组件 (Key Components)

1. **AdminLoginRequest**: 登录请求DTO，包含用户名和密码验证
2. **UserService.adminLogin()**: 处理管理员登录逻辑
3. **JwtUtil.generateTokenWithRole()**: 生成包含角色的JWT token
4. **JwtAuthenticationFilter**: 从token中提取角色并设置权限
5. **SecurityConfig**: 配置登录/登出端点为公开访问

### 认证流程 (Authentication Flow)

1. 用户提交用户名和密码
2. 后端验证凭据和角色
3. 生成包含用户信息和角色的JWT token
4. 返回token和用户信息给前端
5. 前端在后续请求中携带token
6. JwtAuthenticationFilter验证token并设置Spring Security上下文
7. SecurityConfig根据角色控制访问权限

## 常见问题 (FAQ)

**Q: 微信用户和管理员用户有什么区别？**
A: 微信用户通过openId登录，管理员用户通过username/password登录。两种用户类型共用同一个User表，但管理员用户的role字段为"ADMIN"。

**Q: 如何创建新的管理员账户？**
A: 可以直接在数据库中插入记录，确保password字段使用BCrypt加密，role设置为"ADMIN"。

**Q: 忘记管理员密码怎么办？**
A: 需要在数据库中直接更新password字段，使用BCrypt生成新密码的哈希值。

**Q: 可以同时支持微信登录和管理员登录吗？**
A: 可以。微信用户使用 `/user/wechat-login` 端点，管理员用户使用 `/admin/login` 端点。

## 未来改进 (Future Improvements)

1. 添加密码重置功能
2. 实现密码复杂度验证
3. 添加登录失败次数限制
4. 实现管理员账户管理界面
5. 支持多因素认证(MFA)
