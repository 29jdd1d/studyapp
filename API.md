# API接口文档 (API Documentation)

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **请求格式**: JSON
- **响应格式**: JSON

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 1. 用户管理模块 (User Management)

### 1.1 微信登录
- **接口**: `POST /user/wechat-login`
- **描述**: 微信小程序一键登录
- **无需认证**

**请求参数**:
```json
{
  "code": "微信登录凭证",
  "nickName": "昵称",
  "avatarUrl": "头像URL",
  "gender": "性别"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "nickName": "张三",
      "avatarUrl": "https://...",
      "studyDays": 0,
      "studyHours": 0
    }
  }
}
```

### 1.2 获取用户信息
- **接口**: `GET /user/info`
- **描述**: 获取当前登录用户信息
- **需要认证**

### 1.3 更新用户信息
- **接口**: `PUT /user/info`
- **描述**: 更新用户个人信息
- **需要认证**

**请求参数**:
```json
{
  "nickName": "新昵称",
  "phone": "13800138000",
  "email": "user@example.com",
  "targetUniversity": "清华大学",
  "targetMajor": "计算机科学与技术",
  "examYear": "2024"
}
```

### 1.4 获取学习数据看板
- **接口**: `GET /user/dashboard`
- **描述**: 获取学习统计数据
- **需要认证**

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "studyDays": 30,
    "studyHours": 120,
    "completedQuestions": 500,
    "correctQuestions": 450
  }
}
```

## 2. 学习资源管理模块 (Learning Resources)

### 2.1 创建学习资源
- **接口**: `POST /resource`
- **描述**: 上传学习资源
- **需要认证**

**请求参数**:
```json
{
  "title": "高等数学第一章",
  "description": "详细讲解高等数学基础知识",
  "type": "VIDEO",
  "subject": "数学",
  "chapter": "第一章",
  "section": "第一节",
  "fileUrl": "https://...",
  "coverUrl": "https://...",
  "duration": 3600
}
```

### 2.2 分页查询学习资源
- **接口**: `GET /resource/list`
- **描述**: 获取学习资源列表
- **需要认证**

**查询参数**:
- `subject`: 科目（可选）
- `type`: 类型（VIDEO/DOCUMENT/QUESTION_BANK）
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

### 2.3 获取资源详情
- **接口**: `GET /resource/{id}`
- **描述**: 获取资源详细信息
- **需要认证**

### 2.4 按章节获取资源
- **接口**: `GET /resource/chapter`
- **描述**: 按学科和章节获取资源
- **需要认证**

**查询参数**:
- `subject`: 科目
- `chapter`: 章节

## 3. 个性化学习计划模块 (Study Plan)

### 3.1 创建学习计划
- **接口**: `POST /plan`
- **描述**: 手动创建学习计划
- **需要认证**

**请求参数**:
```json
{
  "title": "考研备考计划",
  "description": "针对清华大学计算机专业的学习计划",
  "targetUniversity": "清华大学",
  "targetMajor": "计算机科学与技术",
  "startDate": "2024-01-01",
  "endDate": "2024-12-20"
}
```

### 3.2 生成推荐学习计划
- **接口**: `POST /plan/recommend`
- **描述**: 根据目标自动生成学习计划
- **需要认证**

**查询参数**:
- `targetUniversity`: 目标院校
- `targetMajor`: 目标专业
- `examDate`: 考试日期（yyyy-MM-dd）

### 3.3 获取我的学习计划
- **接口**: `GET /plan/my`
- **描述**: 获取用户的所有学习计划
- **需要认证**

### 3.4 获取活跃的学习计划
- **接口**: `GET /plan/active`
- **描述**: 获取正在进行中的学习计划
- **需要认证**

### 3.5 获取今日计划
- **接口**: `GET /plan/{planId}/today`
- **描述**: 获取今天的学习计划项
- **需要认证**

### 3.6 完成计划项
- **接口**: `POST /plan/item/{itemId}/complete`
- **描述**: 标记学习计划项为已完成
- **需要认证**

**查询参数**:
- `actualHours`: 实际学习时长（小时）

## 4. 题库与刷题模块 (Question Bank)

### 4.1 创建题目
- **接口**: `POST /question`
- **描述**: 添加新题目
- **需要认证（管理员）**

**请求参数**:
```json
{
  "subject": "政治",
  "chapter": "马克思主义基本原理",
  "knowledgePoint": "辩证法",
  "type": "SINGLE_CHOICE",
  "difficulty": "MEDIUM",
  "content": "下列关于辩证法的说法正确的是？",
  "optionA": "选项A",
  "optionB": "选项B",
  "optionC": "选项C",
  "optionD": "选项D",
  "answer": "B",
  "analysis": "解析内容",
  "year": "2023"
}
```

### 4.2 分页查询题目
- **接口**: `GET /question/list`
- **描述**: 获取题目列表
- **需要认证**

**查询参数**:
- `subject`: 科目
- `chapter`: 章节
- `type`: 题型（SINGLE_CHOICE/MULTIPLE_CHOICE/TRUE_FALSE/FILL_BLANK/ESSAY）
- `year`: 年份
- `pageNum`: 页码
- `pageSize`: 每页数量

### 4.3 提交答案
- **接口**: `POST /question/{id}/answer`
- **描述**: 提交答题结果
- **需要认证**

**查询参数**:
- `answer`: 答案
- `timeSpent`: 答题时间（秒）

**响应示例**:
```json
{
  "code": 200,
  "data": true  // true表示正确，false表示错误
}
```

### 4.4 获取错题列表
- **接口**: `GET /question/wrong`
- **描述**: 获取用户的错题
- **需要认证**

**查询参数**:
- `mastered`: 是否已掌握（可选）

### 4.5 标记错题已掌握
- **接口**: `POST /question/wrong/{questionId}/master`
- **描述**: 将错题标记为已掌握
- **需要认证**

### 4.6 获取智能练习题目
- **接口**: `GET /question/smart-practice`
- **描述**: 智能推荐练习题（错题优先）
- **需要认证**

**查询参数**:
- `subject`: 科目
- `count`: 题目数量（默认20）

## 5. 社区交流模块 (Community)

### 5.1 创建帖子
- **接口**: `POST /community/post`
- **描述**: 发布新帖子
- **需要认证**

**请求参数**:
```json
{
  "title": "考研经验分享",
  "content": "帖子内容...",
  "category": "EXPERIENCE",
  "images": "图片1,图片2,图片3"
}
```

**帖子分类**:
- `NEWS`: 资讯
- `EXPERIENCE`: 经验分享
- `DISCUSSION`: 讨论
- `CHECK_IN`: 打卡

### 5.2 获取帖子列表
- **接口**: `GET /community/post/list`
- **描述**: 分页获取帖子
- **需要认证**

**查询参数**:
- `category`: 分类（可选）
- `pageNum`: 页码
- `pageSize`: 每页数量

### 5.3 获取我的帖子
- **接口**: `GET /community/post/my`
- **描述**: 获取我发布的帖子
- **需要认证**

### 5.4 获取置顶帖子
- **接口**: `GET /community/post/pinned`
- **描述**: 获取置顶帖子列表
- **需要认证**

### 5.5 获取帖子详情
- **接口**: `GET /community/post/{id}`
- **描述**: 获取帖子详细内容
- **需要认证**

### 5.6 点赞帖子
- **接口**: `POST /community/post/{id}/like`
- **描述**: 给帖子点赞
- **需要认证**

### 5.7 添加评论
- **接口**: `POST /community/comment`
- **描述**: 评论帖子
- **需要认证**

**请求参数**:
```json
{
  "postId": 1,
  "content": "评论内容",
  "parentId": null  // 如果是回复评论，填写父评论ID
}
```

### 5.8 获取评论列表
- **接口**: `GET /community/comment/{postId}`
- **描述**: 获取帖子的评论
- **需要认证**

### 5.9 学习打卡
- **接口**: `POST /community/checkin`
- **描述**: 每日学习打卡
- **需要认证**

**请求参数**:
```json
{
  "checkInDate": "2024-01-01",
  "studyHours": 8,
  "note": "今天学习了高等数学",
  "images": "图片1,图片2"
}
```

### 5.10 获取打卡记录
- **接口**: `GET /community/checkin/records`
- **描述**: 获取我的打卡历史
- **需要认证**

### 5.11 获取连续打卡天数
- **接口**: `GET /community/checkin/continuous`
- **描述**: 获取连续打卡天数
- **需要认证**

## 6. 系统后台管理模块 (Admin)

### 6.1 获取系统统计数据
- **接口**: `GET /admin/statistics`
- **描述**: 获取系统整体统计信息
- **需要认证（管理员）**

### 6.2 资源管理
- **接口**: `GET /admin/resources` - 获取所有资源
- **接口**: `POST /admin/resource/{id}/publish` - 发布资源
- **接口**: `DELETE /admin/resource/{id}` - 删除资源
- **需要认证（管理员）**

### 6.3 题库管理
- **接口**: `GET /admin/questions` - 获取所有题目
- **接口**: `DELETE /admin/question/{id}` - 删除题目
- **需要认证（管理员）**

### 6.4 社区管理
- **接口**: `GET /admin/posts` - 获取所有帖子
- **接口**: `DELETE /admin/post/{id}` - 删除帖子
- **需要认证（管理员）**

## 错误码说明

- `200`: 成功
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器内部错误

## 认证说明

除了微信登录接口外，所有接口都需要在请求头中携带JWT Token：

```
Authorization: Bearer {your_jwt_token}
```

## 数据库表结构

主要数据表：
1. `sys_user` - 用户表
2. `learning_resource` - 学习资源表
3. `study_plan` - 学习计划表
4. `study_plan_item` - 学习计划项表
5. `question` - 题目表
6. `answer_record` - 答题记录表
7. `wrong_question` - 错题表
8. `community_post` - 社区帖子表
9. `post_comment` - 评论表
10. `study_checkin` - 学习打卡表

## 注意事项

1. 所有时间格式使用 ISO 8601 标准: `yyyy-MM-dd` 或 `yyyy-MM-ddTHH:mm:ss`
2. 文件上传大小限制为 100MB
3. 分页查询默认每页10条，最大100条
4. JWT Token有效期为24小时
