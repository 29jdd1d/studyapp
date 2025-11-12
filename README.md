# 考研学习小程序 (Postgraduate Study Mini Program)

基于SpringBoot的考研学习小程序的设计与实现

## 项目简介

本项目是一个完整的考研学习管理系统，包含微信小程序后端API，支持学习资源管理、个性化学习计划、题库刷题、社区交流等功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security + JWT
- **API文档**: Swagger 3.0
- **构建工具**: Maven

## 核心功能模块

### 1. 用户管理模块
- 微信一键登录
- 个人信息维护
- 学习数据看板

### 2. 学习资源管理模块
- 支持视频、文档、题库等多种资源类型
- 按科目、章节分类管理
- 资源上传、发布与管理

### 3. 个性化学习计划模块
- 根据目标院校和专业生成学习计划
- 动态学习计划推荐
- 进度跟踪与提醒

### 4. 题库与刷题模块
- 按知识点、章节、年份分类
- 在线答题
- 错题自动收录
- 智能练习（错题优先）

### 5. 社区交流模块
- 考研资讯发布
- 备考经验分享
- 学习打卡
- 互动评论

### 6. 系统后台管理模块
- 用户管理
- 资源管理
- 内容审核
- 数据统计与分析

## 项目结构

```
src/main/java/com/studyapp/
├── StudyApplication.java          # 主启动类
├── common/                        # 通用类
│   ├── Result.java               # 统一响应结果
│   └── PageRequest.java          # 分页请求
├── config/                        # 配置类
│   ├── SecurityConfig.java       # 安全配置
│   ├── SwaggerConfig.java        # Swagger配置
│   ├── WebMvcConfig.java         # Web MVC配置
│   └── JwtAuthenticationFilter.java  # JWT过滤器
├── controller/                    # 控制器层
│   ├── UserController.java       # 用户控制器
│   ├── LearningResourceController.java  # 学习资源控制器
│   ├── StudyPlanController.java  # 学习计划控制器
│   ├── QuestionController.java   # 题库控制器
│   ├── CommunityController.java  # 社区控制器
│   └── AdminController.java      # 后台管理控制器
├── entity/                        # 实体类
│   ├── User.java                 # 用户
│   ├── LearningResource.java     # 学习资源
│   ├── StudyPlan.java            # 学习计划
│   ├── StudyPlanItem.java        # 学习计划项
│   ├── Question.java             # 题目
│   ├── AnswerRecord.java         # 答题记录
│   ├── WrongQuestion.java        # 错题
│   ├── CommunityPost.java        # 社区帖子
│   ├── PostComment.java          # 评论
│   └── StudyCheckIn.java         # 学习打卡
├── repository/                    # 数据访问层
├── service/                       # 服务层
│   ├── UserService.java
│   ├── LearningResourceService.java
│   ├── StudyPlanService.java
│   ├── QuestionService.java
│   └── CommunityService.java
├── dto/                          # 数据传输对象
│   ├── WechatLoginRequest.java
│   ├── LoginResponse.java
│   └── UserDTO.java
└── util/                         # 工具类
    └── JwtUtil.java
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE studyapp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyapp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 3. 微信小程序配置

在 `application.yml` 中配置微信小程序的 appid 和 secret：
```yaml
wechat:
  miniapp:
    appid: your_appid
    secret: your_secret
```

### 4. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

项目启动后访问：
- API基础路径: http://localhost:8080/api
- Swagger文档: http://localhost:8080/swagger-ui/

## API接口文档

### 用户管理 (/api/user)
- POST `/wechat-login` - 微信登录
- GET `/info` - 获取用户信息
- PUT `/info` - 更新用户信息
- GET `/dashboard` - 获取学习数据看板

### 学习资源 (/api/resource)
- POST `/` - 创建学习资源
- GET `/list` - 分页查询学习资源
- GET `/{id}` - 获取资源详情
- PUT `/{id}` - 更新学习资源
- DELETE `/{id}` - 删除学习资源

### 学习计划 (/api/plan)
- POST `/` - 创建学习计划
- GET `/my` - 获取我的学习计划
- GET `/active` - 获取活跃的学习计划
- POST `/recommend` - 生成推荐学习计划
- POST `/item/{itemId}/complete` - 完成计划项

### 题库刷题 (/api/question)
- GET `/list` - 分页查询题目
- GET `/{id}` - 获取题目详情
- POST `/{id}/answer` - 提交答案
- GET `/wrong` - 获取错题列表
- GET `/smart-practice` - 获取智能练习题目

### 社区交流 (/api/community)
- POST `/post` - 创建帖子
- GET `/post/list` - 获取帖子列表
- POST `/post/{id}/like` - 点赞帖子
- POST `/comment` - 添加评论
- POST `/checkin` - 学习打卡
- GET `/checkin/continuous` - 获取连续打卡天数

### 后台管理 (/api/admin)
- GET `/statistics` - 获取系统统计数据
- GET `/resources` - 获取所有资源
- GET `/questions` - 获取所有题目
- GET `/posts` - 获取所有帖子

## 数据库设计

项目使用JPA自动创建数据表，主要包括以下表：
- sys_user - 用户表
- learning_resource - 学习资源表
- study_plan - 学习计划表
- study_plan_item - 学习计划项表
- question - 题目表
- answer_record - 答题记录表
- wrong_question - 错题表
- community_post - 社区帖子表
- post_comment - 评论表
- study_checkin - 学习打卡表

## 开发说明

### JWT认证

所有需要认证的接口都需要在请求头中携带JWT token：
```
Authorization: Bearer {token}
```

### 分页参数

分页接口支持以下参数：
- pageNum: 页码（从1开始）
- pageSize: 每页数量

## 注意事项

1. 首次运行时，JPA会自动创建数据表
2. 需要配置正确的微信小程序appid和secret才能使用微信登录
3. JWT secret建议在生产环境中修改为更安全的值
4. 文件上传路径需要确保服务器有写入权限

## 许可证

MIT License
