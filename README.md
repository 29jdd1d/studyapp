# 考研学习小程序 (Postgraduate Study Mini Program)

基于SpringBoot的考研学习小程序的设计与实现

## 项目简介

本项目是一个完整的考研学习管理系统，包含Spring Boot后端API和微信小程序前端，支持学习资源管理、个性化学习计划、题库刷题、社区交流等功能。

## 技术栈

### 后端技术
- **后端框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **缓存**: Redis (Spring Data Redis + Jedis)
- **对象存储**: 腾讯云COS (Cloud Object Storage)
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security + JWT
- **API文档**: Swagger 3.0
- **构建工具**: Maven

### 前端技术
- **框架**: 微信小程序原生框架
- **UI**: 自定义组件库
- **状态管理**: 全局数据管理
- **网络请求**: 封装的request工具
- **认证**: JWT Token

## 核心功能模块

### 1. 用户管理模块
- 微信一键登录
- 个人信息维护
- 学习数据看板

### 2. 学习资源管理模块
- 支持视频、文档、题库等多种资源类型
- 按科目、章节分类管理
- 资源上传、发布与管理
- **腾讯云COS文件存储**（视频、文档、图片）
- **Redis缓存优化**（资源列表、详情缓存）

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

### 后端结构
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
│   ├── CommunityService.java
│   └── CosService.java           # 腾讯云COS服务
├── dto/                          # 数据传输对象
│   ├── WechatLoginRequest.java
│   ├── LoginResponse.java
│   └── UserDTO.java
└── util/                         # 工具类
    └── JwtUtil.java
```

### 前端结构
```
miniprogram/
├── api/                          # API接口封装
│   ├── user.js                  # 用户API
│   ├── resource.js              # 学习资源API
│   ├── plan.js                  # 学习计划API
│   ├── question.js              # 题库API
│   ├── community.js             # 社区API
│   └── upload.js                # 文件上传API
├── pages/                        # 页面文件
│   ├── index/                   # 首页
│   ├── login/                   # 登录页
│   ├── study/                   # 学习页
│   ├── community/               # 社区页
│   ├── profile/                 # 个人中心
│   ├── resource/                # 学习资源相关页面
│   ├── plan/                    # 学习计划相关页面
│   └── question/                # 题库相关页面
├── utils/                        # 工具类
│   └── request.js               # 网络请求封装
├── app.js                        # 小程序入口
├── app.json                      # 小程序配置
├── app.wxss                      # 全局样式
├── config.js                     # 配置文件
└── README.md                     # 前端文档
```

## 快速开始

### 后端启动

#### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+ (可选，用于缓存)
- 腾讯云COS账号 (可选，用于文件存储)

#### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE studyapp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. Redis配置

安装并启动Redis：
```bash
# Linux/Mac
redis-server

# 或使用Docker
docker run -d -p 6379:6379 redis:6.0
```

#### 4. 应用配置

修改 `src/main/resources/application.yml` 中的配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyapp?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0

wechat:
  miniapp:
    appid: your_wechat_appid
    secret: your_wechat_secret

tencent:
  cos:
    secret-id: your_cos_secret_id
    secret-key: your_cos_secret_key
    region: ap-guangzhou
    bucket-name: your_bucket_name
    base-url: https://your_bucket_name.cos.ap-guangzhou.myqcloud.com
```

#### 5. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

项目启动后访问：
- API基础路径: http://localhost:8080/api
- Swagger文档: http://localhost:8080/swagger-ui/

### 前端启动

#### 1. 安装微信开发者工具

下载地址: https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

#### 2. 配置项目

修改 `miniprogram/project.config.json` 中的AppID:
```json
{
  "appid": "your_wechat_appid_here"
}
```

修改 `miniprogram/config.js` 中的API地址:
```javascript
module.exports = {
  baseUrl: 'http://localhost:8080/api'
}
```

#### 3. 导入项目

1. 打开微信开发者工具
2. 点击"导入项目"
3. 选择 `miniprogram` 目录
4. 填写项目信息并导入

#### 4. 运行调试

1. 在开发者工具中点击"编译"
2. 在模拟器中查看效果
3. 或扫码在真机上预览

详细的前端开发指南请查看：
- [前端README](miniprogram/README.md)
- [前端集成指南](MINIPROGRAM_GUIDE.md)
- [前端开发示例](MINIPROGRAM_EXAMPLES.md)

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
