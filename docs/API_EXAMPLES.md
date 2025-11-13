# API使用示例 (API Usage Examples)

本文档提供考研学习小程序后端API的详细使用示例，包括curl命令和Postman集合。

## 目录 (Table of Contents)

- [基础配置](#基础配置)
- [用户管理API](#用户管理api)
- [学习资源API](#学习资源api)
- [学习计划API](#学习计划api)
- [题库API](#题库api)
- [社区API](#社区api)
- [文件上传API](#文件上传api)

## 基础配置 (Basic Configuration)

### 环境变量

```bash
export BASE_URL="http://localhost:8080/api"
export TOKEN="your_jwt_token_here"
```

### 通用响应格式

所有API返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

错误响应：

```json
{
  "code": 500,
  "message": "错误信息",
  "data": null
}
```

## 用户管理API (User Management API)

### 1. 微信登录

**端点**: `POST /user/wechat-login`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/user/wechat-login" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "wx_login_code_from_miniprogram"
  }'
```

**请求示例** (Postman):

```json
POST {{baseUrl}}/user/wechat-login
Content-Type: application/json

{
  "code": "wx_login_code_from_miniprogram"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userInfo": {
      "id": 1,
      "openid": "oXXXX",
      "nickname": "张三",
      "avatar": "https://...",
      "phone": null
    }
  }
}
```

### 2. 获取用户信息

**端点**: `GET /user/info`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/user/info" \
  -H "Authorization: Bearer ${TOKEN}"
```

**请求示例** (Postman):

```
GET {{baseUrl}}/user/info
Authorization: Bearer {{token}}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "openid": "oXXXX",
    "nickname": "张三",
    "avatar": "https://...",
    "phone": "13800138000",
    "targetSchool": "清华大学",
    "targetMajor": "计算机科学与技术"
  }
}
```

### 3. 更新用户信息

**端点**: `PUT /user/info`

**请求示例** (curl):

```bash
curl -X PUT "${BASE_URL}/user/info" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "李四",
    "phone": "13900139000",
    "targetSchool": "北京大学",
    "targetMajor": "软件工程"
  }'
```

**请求示例** (Postman):

```json
PUT {{baseUrl}}/user/info
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nickname": "李四",
  "phone": "13900139000",
  "targetSchool": "北京大学",
  "targetMajor": "软件工程"
}
```

### 4. 获取学习数据看板

**端点**: `GET /user/dashboard`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/user/dashboard" \
  -H "Authorization: Bearer ${TOKEN}"
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "studyDays": 45,
    "totalStudyTime": 120,
    "completedPlanItems": 78,
    "answeredQuestions": 1234,
    "correctRate": 0.85
  }
}
```

## 学习资源API (Learning Resource API)

### 1. 分页查询学习资源

**端点**: `GET /resource/list`

**请求参数**:
- `subject`: 科目 (政治/英语/数学/专业课)
- `type`: 类型 (VIDEO/DOCUMENT/QUESTION)
- `pageNum`: 页码 (从1开始)
- `pageSize`: 每页数量

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/resource/list?subject=数学&type=VIDEO&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

**请求示例** (Postman):

```
GET {{baseUrl}}/resource/list?subject=数学&type=VIDEO&pageNum=1&pageSize=10
Authorization: Bearer {{token}}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 45,
    "list": [
      {
        "id": 1,
        "title": "高等数学-导数与微分",
        "subject": "数学",
        "type": "VIDEO",
        "chapter": "第一章",
        "description": "详细讲解导数的概念和计算方法",
        "coverUrl": "https://...",
        "fileUrl": "https://...",
        "duration": 45,
        "viewCount": 1234,
        "likeCount": 89
      }
    ]
  }
}
```

### 2. 获取资源详情

**端点**: `GET /resource/{id}`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/resource/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**请求示例** (Postman):

```
GET {{baseUrl}}/resource/1
Authorization: Bearer {{token}}
```

### 3. 创建学习资源 (管理员)

**端点**: `POST /resource`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/resource" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "线性代数-矩阵运算",
    "subject": "数学",
    "type": "VIDEO",
    "chapter": "第二章",
    "description": "矩阵的基本运算方法",
    "coverUrl": "https://example.com/cover.jpg",
    "fileUrl": "https://example.com/video.mp4",
    "duration": 30,
    "tags": ["线性代数", "矩阵"]
  }'
```

## 学习计划API (Study Plan API)

### 1. 创建学习计划

**端点**: `POST /plan`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/plan" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "数学复习计划",
    "description": "系统复习高等数学和线性代数",
    "targetDate": "2024-12-31",
    "items": [
      {
        "title": "高等数学第一章",
        "planDate": "2024-01-15",
        "estimatedTime": 120
      },
      {
        "title": "高等数学第二章",
        "planDate": "2024-01-20",
        "estimatedTime": 150
      }
    ]
  }'
```

### 2. 获取我的学习计划

**端点**: `GET /plan/my`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/plan/my" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 3. 获取今日任务

**端点**: `GET /plan/today`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/plan/today" \
  -H "Authorization: Bearer ${TOKEN}"
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 123,
      "title": "高等数学第三章习题",
      "planDate": "2024-01-25",
      "estimatedTime": 90,
      "completed": false
    }
  ]
}
```

### 4. 完成计划项

**端点**: `POST /plan/item/{itemId}/complete`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/plan/item/123/complete" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "actualTime": 85,
    "notes": "完成了所有习题，重点掌握了极限的计算"
  }'
```

### 5. 生成推荐学习计划

**端点**: `POST /plan/recommend`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/plan/recommend" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "targetDate": "2024-12-31",
    "subjects": ["数学", "英语", "政治"],
    "dailyStudyTime": 4
  }'
```

## 题库API (Question Bank API)

### 1. 分页查询题目

**端点**: `GET /question/list`

**请求参数**:
- `subject`: 科目
- `chapter`: 章节
- `type`: 题型 (SINGLE_CHOICE/MULTIPLE_CHOICE/TRUE_FALSE/FILL_BLANK/SUBJECTIVE)
- `year`: 年份
- `pageNum`: 页码
- `pageSize`: 每页数量

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/question/list?subject=数学&chapter=第一章&type=SINGLE_CHOICE&pageNum=1&pageSize=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 2. 获取题目详情

**端点**: `GET /question/{id}`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/question/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "subject": "数学",
    "chapter": "第一章",
    "type": "SINGLE_CHOICE",
    "content": "函数f(x)=x²在x=0处的导数是？",
    "options": ["A. 0", "B. 1", "C. 2", "D. 不存在"],
    "answer": "A",
    "analysis": "根据导数定义...",
    "difficulty": 2,
    "tags": ["导数", "基础"]
  }
}
```

### 3. 提交答案

**端点**: `POST /question/{id}/answer`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/question/1/answer" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "answer": "A",
    "timeSpent": 45
  }'
```

**响应示例**:

```json
{
  "code": 200,
  "message": "答题成功",
  "data": {
    "correct": true,
    "correctAnswer": "A",
    "analysis": "根据导数定义...",
    "score": 5
  }
}
```

### 4. 获取错题列表

**端点**: `GET /question/wrong`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/question/wrong?pageNum=1&pageSize=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 5. 智能练习

**端点**: `GET /question/smart-practice`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/question/smart-practice?subject=数学&count=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 社区API (Community API)

### 1. 创建帖子

**端点**: `POST /community/post`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/community/post" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "考研数学复习经验分享",
    "content": "经过三个月的努力，终于完成了数学一轮复习...",
    "category": "EXPERIENCE",
    "tags": ["数学", "经验分享"],
    "images": ["https://...", "https://..."]
  }'
```

### 2. 获取帖子列表

**端点**: `GET /community/post/list`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/community/post/list?category=EXPERIENCE&pageNum=1&pageSize=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 3. 点赞帖子

**端点**: `POST /community/post/{id}/like`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/community/post/1/like" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4. 添加评论

**端点**: `POST /community/comment`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/community/comment" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "postId": 1,
    "content": "感谢分享，很有帮助！"
  }'
```

### 5. 学习打卡

**端点**: `POST /community/checkin`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/community/checkin" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "studyTime": 240,
    "content": "今天学习了高等数学第三章，完成了30道练习题",
    "mood": "HAPPY"
  }'
```

### 6. 获取连续打卡天数

**端点**: `GET /community/checkin/continuous`

**请求示例** (curl):

```bash
curl -X GET "${BASE_URL}/community/checkin/continuous" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 文件上传API (File Upload API)

### 1. 上传图片

**端点**: `POST /file/upload/image`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/file/upload/image" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "file=@/path/to/image.jpg"
```

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://your-bucket.cos.ap-guangzhou.myqcloud.com/images/uuid.jpg",
    "filename": "uuid.jpg",
    "size": 102400
  }
}
```

### 2. 上传视频

**端点**: `POST /file/upload/video`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/file/upload/video" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "file=@/path/to/video.mp4"
```

### 3. 上传文档

**端点**: `POST /file/upload/document`

**请求示例** (curl):

```bash
curl -X POST "${BASE_URL}/file/upload/document" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "file=@/path/to/document.pdf"
```

## Postman集合 (Postman Collection)

### 环境变量设置

在Postman中创建环境并设置以下变量：

```json
{
  "baseUrl": "http://localhost:8080/api",
  "token": "your_jwt_token_here"
}
```

### 批量测试脚本

在Collection的Pre-request Script中添加：

```javascript
// 自动设置认证头
pm.request.headers.add({
  key: 'Authorization',
  value: 'Bearer ' + pm.environment.get('token')
});
```

在Collection的Tests中添加：

```javascript
// 验证响应状态
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证响应格式
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('data');
});

// 保存token
if (pm.response.json().data && pm.response.json().data.token) {
    pm.environment.set("token", pm.response.json().data.token);
}
```

## 完整测试流程示例

```bash
#!/bin/bash
# 完整的API测试流程

BASE_URL="http://localhost:8080/api"

# 1. 微信登录
echo "=== 1. Login ==="
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/user/wechat-login" \
  -H "Content-Type: application/json" \
  -d '{"code": "test_code"}')
  
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
echo "Token: $TOKEN"

# 2. 获取用户信息
echo -e "\n=== 2. Get User Info ==="
curl -s -X GET "${BASE_URL}/user/info" \
  -H "Authorization: Bearer ${TOKEN}" | jq

# 3. 获取学习资源
echo -e "\n=== 3. Get Resources ==="
curl -s -X GET "${BASE_URL}/resource/list?subject=数学&pageNum=1&pageSize=5" \
  -H "Authorization: Bearer ${TOKEN}" | jq

# 4. 创建学习计划
echo -e "\n=== 4. Create Study Plan ==="
curl -s -X POST "${BASE_URL}/plan" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试计划",
    "description": "测试学习计划创建",
    "targetDate": "2024-12-31"
  }' | jq

# 5. 学习打卡
echo -e "\n=== 5. Check In ==="
curl -s -X POST "${BASE_URL}/community/checkin" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "studyTime": 120,
    "content": "测试打卡",
    "mood": "HAPPY"
  }' | jq

echo -e "\n=== Test Complete ==="
```

## 注意事项 (Notes)

1. **认证**: 除了登录接口，所有接口都需要JWT token
2. **分页**: pageNum从1开始，pageSize建议10-50
3. **时间格式**: 使用ISO 8601格式，如 "2024-01-25T10:30:00"
4. **文件大小**: 单个文件不超过100MB
5. **速率限制**: 建议每秒不超过100次请求

---

更多信息请参考 [API文档](../API.md) 和 [Swagger UI](http://localhost:8080/swagger-ui/)
