# 微信小程序前端集成指南

## 项目概述

本文档说明如何将微信小程序前端与Spring Boot后端API集成使用。

## 目录结构

```
studyapp/
├── src/                          # Spring Boot后端
│   └── main/
│       └── java/com/studyapp/
│           ├── controller/       # API控制器
│           ├── service/          # 业务服务
│           └── entity/           # 数据实体
├── miniprogram/                  # 微信小程序前端
│   ├── api/                      # API接口封装
│   ├── pages/                    # 页面文件
│   ├── utils/                    # 工具类
│   ├── app.js                    # 小程序入口
│   ├── app.json                  # 小程序配置
│   └── config.js                 # 配置文件
├── API.md                        # API接口文档
└── README.md                     # 项目说明
```

## 快速开始

### 1. 后端配置

#### 1.1 配置微信小程序参数

在 `src/main/resources/application.yml` 中配置：

```yaml
wechat:
  miniapp:
    appid: your_wechat_appid      # 微信小程序AppID
    secret: your_wechat_secret    # 微信小程序AppSecret
```

#### 1.2 启动后端服务

```bash
cd studyapp
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 2. 前端配置

#### 2.1 安装微信开发者工具

下载地址: https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

#### 2.2 配置小程序AppID

修改 `miniprogram/project.config.json`:

```json
{
  "appid": "your_wechat_appid_here"
}
```

#### 2.3 配置API地址

修改 `miniprogram/config.js`:

```javascript
module.exports = {
  baseUrl: 'http://localhost:8080/api',  // 后端API地址
  wechat: {
    appId: 'your_wechat_appid_here'
  }
}
```

**注意**: 如果后端部署在服务器上，需要修改为实际的服务器地址，例如：
```javascript
baseUrl: 'https://your-domain.com/api'
```

#### 2.4 导入项目

1. 打开微信开发者工具
2. 点击"导入项目"
3. 选择 `miniprogram` 目录
4. 填写项目名称
5. 点击"导入"

### 3. 配置合法域名

在微信公众平台（mp.weixin.qq.com）配置服务器域名：

1. 登录微信公众平台
2. 进入"开发" -> "开发设置"
3. 在"服务器域名"中配置：
   - **request合法域名**: `https://your-domain.com`
   - **uploadFile合法域名**: `https://your-domain.com`
   - **downloadFile合法域名**: `https://your-domain.com`

**注意**: 线上环境必须使用HTTPS协议！

## API接口说明

### 认证机制

除登录接口外，所有API请求都需要携带JWT Token：

```javascript
// 小程序自动处理，在 utils/request.js 中实现
header: {
  'Authorization': 'Bearer ' + token
}
```

### 主要接口模块

#### 1. 用户管理 (/user)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/user/wechat-login` | POST | 微信登录 | ❌ |
| `/user/info` | GET | 获取用户信息 | ✅ |
| `/user/info` | PUT | 更新用户信息 | ✅ |
| `/user/dashboard` | GET | 学习数据看板 | ✅ |

#### 2. 学习资源 (/resource)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/resource/list` | GET | 资源列表 | ✅ |
| `/resource/{id}` | GET | 资源详情 | ✅ |
| `/resource/chapter` | GET | 按章节获取 | ✅ |

#### 3. 学习计划 (/plan)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/plan` | POST | 创建计划 | ✅ |
| `/plan/my` | GET | 我的计划 | ✅ |
| `/plan/active` | GET | 活跃计划 | ✅ |
| `/plan/{planId}/today` | GET | 今日计划 | ✅ |
| `/plan/item/{itemId}/complete` | POST | 完成计划项 | ✅ |

#### 4. 题库 (/question)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/question/list` | GET | 题目列表 | ✅ |
| `/question/{id}/answer` | POST | 提交答案 | ✅ |
| `/question/wrong` | GET | 错题列表 | ✅ |
| `/question/smart-practice` | GET | 智能练习 | ✅ |

#### 5. 社区 (/community)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/community/post` | POST | 发布帖子 | ✅ |
| `/community/post/list` | GET | 帖子列表 | ✅ |
| `/community/post/{id}` | GET | 帖子详情 | ✅ |
| `/community/post/{id}/like` | POST | 点赞 | ✅ |
| `/community/comment` | POST | 添加评论 | ✅ |
| `/community/checkin` | POST | 学习打卡 | ✅ |

#### 6. 文件上传 (/file/upload)

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/file/upload/image` | POST | 上传图片 | ✅ |
| `/file/upload/video` | POST | 上传视频 | ✅ |
| `/file/upload/document` | POST | 上传文档 | ✅ |

## 使用示例

### 登录流程

```javascript
// 在 pages/login/login.js 中
const userApi = require('../../api/user.js');

// 1. 获取微信登录凭证
wx.login({
  success: (res) => {
    const code = res.code;
    
    // 2. 获取用户信息
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: (userRes) => {
        // 3. 调用后端登录接口
        userApi.wechatLogin({
          code: code,
          nickName: userRes.userInfo.nickName,
          avatarUrl: userRes.userInfo.avatarUrl,
          gender: userRes.userInfo.gender
        }).then(result => {
          // 4. 保存token和用户信息
          app.saveLoginInfo(result.token, result.user);
          // 5. 跳转到首页
          wx.switchTab({ url: '/pages/index/index' });
        });
      }
    });
  }
});
```

### 调用API

```javascript
// 在页面中调用API
const resourceApi = require('../../api/resource.js');

// 获取学习资源列表
resourceApi.getResourceList({
  pageNum: 1,
  pageSize: 10,
  subject: '数学'
}).then(result => {
  console.log('资源列表:', result.list);
}).catch(err => {
  console.error('获取失败:', err);
});
```

### 上传文件

```javascript
const uploadApi = require('../../api/upload.js');

// 选择并上传图片
wx.chooseImage({
  count: 1,
  success: (res) => {
    const filePath = res.tempFilePaths[0];
    
    uploadApi.uploadImage(filePath).then(data => {
      console.log('上传成功，URL:', data.url);
    }).catch(err => {
      console.error('上传失败:', err);
    });
  }
});
```

## 开发调试

### 1. 本地调试

在微信开发者工具中：

1. 点击"详情" -> "本地设置"
2. 勾选"不校验合法域名、web-view（业务域名）、TLS版本以及HTTPS证书"
3. 这样可以在开发环境中请求 `http://localhost:8080` 的后端服务

### 2. 网络请求调试

在控制台中查看网络请求：

1. 点击"调试器"标签
2. 选择"Network"面板
3. 查看所有API请求和响应

### 3. 数据调试

在控制台中查看数据：

```javascript
console.log('用户信息:', this.data.userInfo);
console.log('资源列表:', this.data.resources);
```

## 部署上线

### 1. 后端部署

1. 将后端打包
```bash
mvn clean package
```

2. 部署到服务器，确保：
   - 使用HTTPS协议
   - 配置正确的域名
   - 开放8080端口（或使用Nginx反向代理）

### 2. 前端配置

1. 修改 `config.js` 中的 `baseUrl` 为线上地址
2. 在微信公众平台配置服务器域名
3. 在微信开发者工具中上传代码
4. 提交审核
5. 审核通过后发布

## 常见问题

### Q1: 请求失败，提示"不在合法域名列表中"

**A**: 需要在微信公众平台配置服务器域名，或在开发环境中关闭域名校验。

### Q2: 登录失败

**A**: 检查：
1. 后端是否正确配置了微信AppID和AppSecret
2. 网络请求是否正常
3. token是否正确保存

### Q3: 图片上传失败

**A**: 检查：
1. 文件大小是否超过限制
2. 腾讯云COS是否正确配置
3. 上传接口是否有权限

### Q4: 真机预览无法访问后端

**A**: 
1. 确保手机和电脑在同一网络
2. 使用电脑的局域网IP而不是localhost
3. 或者部署到外网服务器

## 技术支持

- 微信小程序官方文档: https://developers.weixin.qq.com/miniprogram/dev/framework/
- Spring Boot文档: https://spring.io/projects/spring-boot
- 项目API文档: ../API.md

## 更新日志

### v1.0.0 (2024-11-12)
- ✅ 完成微信小程序前端基础框架
- ✅ 实现用户登录和认证
- ✅ 完成主要功能模块页面
- ✅ 集成后端API接口
- ✅ 添加完整的文档说明
