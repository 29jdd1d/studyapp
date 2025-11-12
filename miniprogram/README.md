# 考研学习小程序前端

基于微信小程序框架开发的考研学习助手前端界面，配合后端Spring Boot API使用。

## 项目结构

```
miniprogram/
├── api/                      # API接口封装
│   ├── user.js              # 用户相关API
│   ├── resource.js          # 学习资源API
│   ├── plan.js              # 学习计划API
│   ├── question.js          # 题库API
│   ├── community.js         # 社区API
│   └── upload.js            # 文件上传API
├── pages/                    # 页面文件
│   ├── index/               # 首页
│   ├── login/               # 登录页
│   ├── study/               # 学习页
│   ├── community/           # 社区页
│   ├── profile/             # 个人中心页
│   ├── resource/            # 学习资源页面
│   │   ├── list/           # 资源列表
│   │   └── detail/         # 资源详情
│   ├── plan/                # 学习计划页面
│   │   ├── list/           # 计划列表
│   │   ├── detail/         # 计划详情
│   │   └── create/         # 创建计划
│   ├── question/            # 题库页面
│   │   ├── list/           # 题目列表
│   │   ├── practice/       # 练习答题
│   │   └── wrong/          # 错题本
│   └── community/           # 社区页面
│       ├── post/           # 帖子相关
│       │   ├── list/       # 帖子列表
│       │   ├── detail/     # 帖子详情
│       │   └── create/     # 发布帖子
│       └── checkin/        # 学习打卡
├── utils/                    # 工具类
│   └── request.js           # 网络请求封装
├── app.js                    # 小程序主入口
├── app.json                  # 小程序配置
├── app.wxss                  # 全局样式
├── project.config.json       # 项目配置
└── sitemap.json             # 站点地图配置
```

## 核心功能

### 1. 用户管理
- 微信一键登录
- 用户信息管理
- 学习数据看板

### 2. 学习资源
- 学习资源浏览
- 按科目、章节筛选
- 资源详情查看
- 视频、文档学习

### 3. 学习计划
- 创建学习计划
- 智能推荐计划
- 今日任务管理
- 学习进度跟踪

### 4. 题库刷题
- 题目列表浏览
- 在线答题练习
- 错题本管理
- 智能练习推荐

### 5. 社区交流
- 帖子浏览发布
- 评论互动
- 学习打卡
- 经验分享

## 技术特点

### 1. 模块化设计
- API接口统一封装
- 页面组件化开发
- 样式复用优化

### 2. 用户体验
- 统一的UI设计风格
- 流畅的页面交互
- 完善的加载状态
- 友好的错误提示

### 3. 数据管理
- JWT Token认证
- 本地数据缓存
- 全局状态管理

### 4. 网络请求
- 统一的请求封装
- 自动token处理
- 错误统一处理
- 加载状态管理

## 快速开始

### 1. 环境准备

- 安装微信开发者工具
- 注册微信小程序账号
- 获取AppID和AppSecret

### 2. 配置项目

修改 `project.config.json` 中的 `appid`:
```json
{
  "appid": "your_wechat_appid_here"
}
```

修改 `utils/request.js` 中的后端API地址:
```javascript
const BASE_URL = 'http://your-backend-url/api';
```

### 3. 导入项目

1. 打开微信开发者工具
2. 选择"导入项目"
3. 选择 `miniprogram` 目录
4. 填写项目信息
5. 点击"导入"

### 4. 启动项目

1. 在微信开发者工具中打开项目
2. 点击"编译"按钮
3. 在模拟器或真机中预览

## API配置

小程序通过以下API与后端通信：

### 基础配置
- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **请求格式**: JSON
- **响应格式**: JSON

### 主要接口模块

1. **用户模块** (`/user`)
   - 微信登录
   - 获取/更新用户信息
   - 学习数据看板

2. **学习资源** (`/resource`)
   - 资源列表查询
   - 资源详情获取
   - 按章节筛选

3. **学习计划** (`/plan`)
   - 创建计划
   - 获取计划列表
   - 今日任务
   - 完成任务

4. **题库** (`/question`)
   - 题目列表
   - 提交答案
   - 错题管理
   - 智能练习

5. **社区** (`/community`)
   - 帖子发布
   - 评论互动
   - 学习打卡
   - 连续打卡统计

6. **文件上传** (`/file/upload`)
   - 图片上传
   - 视频上传
   - 文档上传

## 页面说明

### 首页 (index)
- 用户信息展示
- 学习数据统计
- 功能快捷入口
- 置顶公告展示

### 学习页 (study)
- 学习资源列表
- 我的学习计划
- 今日学习任务
- 三个标签页切换

### 社区页 (community)
- 帖子列表浏览
- 分类筛选
- 发布新帖
- 点赞评论

### 个人中心 (profile)
- 用户信息
- 学习统计
- 功能菜单
- 退出登录

## 开发说明

### 添加新页面

1. 在 `pages` 目录下创建页面文件夹
2. 创建 `.js`, `.json`, `.wxml`, `.wxss` 四个文件
3. 在 `app.json` 的 `pages` 数组中注册页面

### 调用API

```javascript
const userApi = require('../../api/user.js');

// 获取用户信息
userApi.getUserInfo().then(userInfo => {
  console.log(userInfo);
}).catch(err => {
  console.error(err);
});
```

### 上传文件

```javascript
const uploadApi = require('../../api/upload.js');

wx.chooseImage({
  success: (res) => {
    uploadApi.uploadImage(res.tempFilePaths[0]).then(data => {
      console.log('上传成功', data.url);
    });
  }
});
```

## 注意事项

1. **AppID配置**: 确保在 `project.config.json` 中配置正确的微信小程序AppID

2. **后端地址**: 在 `utils/request.js` 中配置正确的后端API地址

3. **登录凭证**: 微信登录需要配置后端的微信AppID和AppSecret

4. **域名配置**: 在微信公众平台配置服务器域名白名单

5. **HTTPS要求**: 线上环境必须使用HTTPS协议

6. **图片资源**: 需要准备以下图标和图片：
   - Tab栏图标 (home, study, community, profile)
   - 功能图标 (resource, plan, question, checkin等)
   - Logo和默认头像

## 发布上线

### 1. 代码审核

1. 完成开发和测试
2. 点击"上传"按钮上传代码
3. 在微信公众平台提交审核

### 2. 版本管理

- 遵循语义化版本号
- 填写版本说明
- 记录更新内容

### 3. 发布流程

1. 提交代码审核
2. 等待审核通过
3. 设置发布时间
4. 正式发布上线

## 常见问题

### Q: 如何测试微信登录？
A: 在开发者工具中可以使用测试账号进行登录测试。

### Q: 如何处理网络请求错误？
A: 请求封装已包含统一的错误处理，会自动显示错误提示。

### Q: 如何调试页面？
A: 使用微信开发者工具的调试功能，可以查看Console、Network等信息。

### Q: 如何优化性能？
A: 
- 使用分页加载
- 图片懒加载
- 减少不必要的请求
- 合理使用缓存

## 后续优化

- [ ] 添加离线缓存功能
- [ ] 优化图片加载性能
- [ ] 增加更多交互动画
- [ ] 完善错误处理机制
- [ ] 添加数据埋点统计
- [ ] 支持多主题切换

## 技术支持

如有问题，请查看：
- [微信小程序官方文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
- 后端API文档: `../API.md`
- 项目README: `../README.md`

## 许可证

MIT License
