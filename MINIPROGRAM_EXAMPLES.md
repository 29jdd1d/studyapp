# 微信小程序开发示例

本文档提供一些具体的代码示例，帮助开发者快速上手和扩展功能。

## 示例1: 创建新页面

### 步骤1: 创建页面文件

在 `pages/` 目录下创建新页面文件夹，例如 `pages/course/detail/`:

```
pages/course/detail/
├── detail.js      # 页面逻辑
├── detail.json    # 页面配置
├── detail.wxml    # 页面结构
└── detail.wxss    # 页面样式
```

### 步骤2: 编写页面逻辑 (detail.js)

```javascript
// pages/course/detail/detail.js
const resourceApi = require('../../../api/resource.js');

Page({
  data: {
    course: null,
    loading: true
  },

  onLoad: function(options) {
    const courseId = options.id;
    this.loadCourseDetail(courseId);
  },

  loadCourseDetail: function(id) {
    this.setData({ loading: true });
    
    resourceApi.getResourceDetail(id)
      .then(course => {
        this.setData({
          course: course,
          loading: false
        });
      })
      .catch(err => {
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  // 播放视频
  playVideo: function() {
    wx.navigateTo({
      url: `/pages/video/player/player?url=${this.data.course.fileUrl}`
    });
  }
});
```

### 步骤3: 编写页面结构 (detail.wxml)

```xml
<!--pages/course/detail/detail.wxml-->
<view class="container">
  <view class="course-detail" wx:if="{{!loading && course}}">
    <!-- 封面图 -->
    <image class="course-cover" src="{{course.coverUrl}}" mode="aspectFill"></image>
    
    <!-- 课程信息 -->
    <view class="course-info card">
      <text class="course-title">{{course.title}}</text>
      <text class="course-desc">{{course.description}}</text>
      
      <view class="course-meta">
        <text class="tag tag-primary">{{course.subject}}</text>
        <text class="meta-text">{{course.chapter}}</text>
      </view>
    </view>
    
    <!-- 操作按钮 -->
    <button class="play-btn btn-primary" bindtap="playVideo">
      开始学习
    </button>
  </view>
  
  <!-- 加载状态 -->
  <view class="loading" wx:if="{{loading}}">
    <text>加载中...</text>
  </view>
</view>
```

### 步骤4: 编写样式 (detail.wxss)

```css
/* pages/course/detail/detail.wxss */
.course-cover {
  width: 100%;
  height: 400rpx;
}

.course-info {
  margin: 20rpx 30rpx;
  padding: 30rpx;
}

.course-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.course-desc {
  display: block;
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.meta-text {
  font-size: 24rpx;
  color: #999;
}

.play-btn {
  margin: 0 30rpx;
  margin-top: 40rpx;
}
```

### 步骤5: 注册页面

在 `app.json` 的 `pages` 数组中添加：

```json
{
  "pages": [
    "pages/course/detail/detail"
  ]
}
```

## 示例2: 添加新的API接口

### 创建 API 文件 (api/course.js)

```javascript
// api/course.js
const { get, post } = require('../utils/request.js');

/**
 * 获取课程列表
 */
function getCourseList(params) {
  return get('/course/list', params);
}

/**
 * 获取课程详情
 */
function getCourseDetail(id) {
  return get(`/course/${id}`);
}

/**
 * 收藏课程
 */
function favoriteCourse(id) {
  return post(`/course/${id}/favorite`);
}

/**
 * 记录学习进度
 */
function recordProgress(id, progress) {
  return post(`/course/${id}/progress`, { progress });
}

module.exports = {
  getCourseList,
  getCourseDetail,
  favoriteCourse,
  recordProgress
};
```

### 在页面中使用

```javascript
const courseApi = require('../../api/course.js');

Page({
  data: {
    courses: []
  },

  onLoad: function() {
    this.loadCourses();
  },

  loadCourses: function() {
    courseApi.getCourseList({
      pageNum: 1,
      pageSize: 10
    }).then(result => {
      this.setData({
        courses: result.list
      });
    });
  },

  onCourseTap: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/course/detail/detail?id=${id}`
    });
  }
});
```

## 示例3: 表单提交

### 创建表单页面

```javascript
// pages/feedback/feedback.js
const { post } = require('../../utils/request.js');

Page({
  data: {
    formData: {
      type: 'SUGGESTION',
      content: '',
      contact: ''
    }
  },

  // 选择反馈类型
  onTypeChange: function(e) {
    this.setData({
      'formData.type': e.detail.value
    });
  },

  // 输入内容
  onContentInput: function(e) {
    this.setData({
      'formData.content': e.detail.value
    });
  },

  // 输入联系方式
  onContactInput: function(e) {
    this.setData({
      'formData.contact': e.detail.value
    });
  },

  // 提交表单
  submitForm: function() {
    const { type, content, contact } = this.data.formData;

    // 验证
    if (!content.trim()) {
      wx.showToast({
        title: '请输入反馈内容',
        icon: 'none'
      });
      return;
    }

    // 提交
    post('/feedback', {
      type: type,
      content: content,
      contact: contact
    }).then(() => {
      wx.showToast({
        title: '提交成功',
        icon: 'success'
      });
      
      // 返回上一页
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(err => {
      wx.showToast({
        title: '提交失败',
        icon: 'none'
      });
    });
  }
});
```

### 表单页面结构

```xml
<!--pages/feedback/feedback.wxml-->
<view class="container">
  <view class="form card">
    <!-- 反馈类型 -->
    <view class="form-item">
      <text class="label">反馈类型</text>
      <picker mode="selector" 
              range="{{['建议', '问题', '其他']}}" 
              bindchange="onTypeChange">
        <view class="picker">
          {{formData.type === 'SUGGESTION' ? '建议' : formData.type === 'ISSUE' ? '问题' : '其他'}}
        </view>
      </picker>
    </view>

    <!-- 反馈内容 -->
    <view class="form-item">
      <text class="label">反馈内容</text>
      <textarea 
        class="textarea" 
        placeholder="请输入您的反馈内容"
        value="{{formData.content}}"
        bindinput="onContentInput"
        maxlength="500"
      ></textarea>
    </view>

    <!-- 联系方式 -->
    <view class="form-item">
      <text class="label">联系方式（可选）</text>
      <input 
        class="input" 
        placeholder="请输入联系方式"
        value="{{formData.contact}}"
        bindinput="onContactInput"
      />
    </view>

    <!-- 提交按钮 -->
    <button class="submit-btn btn-primary" bindtap="submitForm">
      提交反馈
    </button>
  </view>
</view>
```

## 示例4: 列表加载更多

```javascript
// pages/article/list/list.js
Page({
  data: {
    articles: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad: function() {
    this.loadArticles();
  },

  // 加载文章列表
  loadArticles: function() {
    if (this.data.loading || !this.data.hasMore) {
      return;
    }

    this.setData({ loading: true });

    articleApi.getList({
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }).then(result => {
      const articles = this.data.pageNum === 1 
        ? result.list 
        : [...this.data.articles, ...result.list];

      this.setData({
        articles: articles,
        loading: false,
        hasMore: result.hasNextPage
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      articles: []
    });
    this.loadArticles();
    wx.stopPullDownRefresh();
  },

  // 上拉加载更多
  onReachBottom: function() {
    if (this.data.hasMore) {
      this.setData({
        pageNum: this.data.pageNum + 1
      });
      this.loadArticles();
    }
  }
});
```

## 示例5: 图片上传和预览

```javascript
// pages/post/create/create.js
const uploadApi = require('../../../api/upload.js');

Page({
  data: {
    images: [],  // 已上传的图片URL
    maxImages: 9
  },

  // 选择图片
  chooseImage: function() {
    const remaining = this.data.maxImages - this.data.images.length;
    
    if (remaining <= 0) {
      wx.showToast({
        title: `最多上传${this.data.maxImages}张图片`,
        icon: 'none'
      });
      return;
    }

    wx.chooseImage({
      count: remaining,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.uploadImages(res.tempFilePaths);
      }
    });
  },

  // 上传图片
  uploadImages: function(filePaths) {
    wx.showLoading({ title: '上传中...' });

    const uploadPromises = filePaths.map(path => {
      return uploadApi.uploadImage(path);
    });

    Promise.all(uploadPromises)
      .then(results => {
        wx.hideLoading();
        const imageUrls = results.map(r => r.url);
        this.setData({
          images: [...this.data.images, ...imageUrls]
        });
      })
      .catch(() => {
        wx.hideLoading();
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
      });
  },

  // 预览图片
  previewImage: function(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.images
    });
  },

  // 删除图片
  deleteImage: function(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images });
  }
});
```

## 最佳实践

### 1. 错误处理

```javascript
// 统一的错误处理
function handleError(err, defaultMessage = '操作失败') {
  console.error(err);
  wx.showToast({
    title: err.message || defaultMessage,
    icon: 'none'
  });
}

// 使用
someApi.doSomething()
  .then(result => {
    // 处理成功
  })
  .catch(err => handleError(err, '加载失败'));
```

### 2. 数据缓存

```javascript
// 缓存数据
function cacheData(key, data, expire = 3600000) {
  const cacheItem = {
    data: data,
    expire: Date.now() + expire
  };
  wx.setStorageSync(key, cacheItem);
}

// 获取缓存
function getCachedData(key) {
  const cacheItem = wx.getStorageSync(key);
  if (!cacheItem) return null;
  
  if (Date.now() > cacheItem.expire) {
    wx.removeStorageSync(key);
    return null;
  }
  
  return cacheItem.data;
}
```

### 3. 节流防抖

```javascript
// 节流函数
function throttle(fn, delay = 500) {
  let timer = null;
  return function() {
    if (timer) return;
    timer = setTimeout(() => {
      fn.apply(this, arguments);
      timer = null;
    }, delay);
  };
}

// 使用
Page({
  onSearchInput: throttle(function(e) {
    const keyword = e.detail.value;
    this.search(keyword);
  }, 300)
});
```

## 调试技巧

### 1. 使用 console

```javascript
console.log('数据:', data);
console.warn('警告:', warning);
console.error('错误:', error);
```

### 2. 使用调试器

在微信开发者工具中：
- 点击"调试器"
- 查看Console、Network、Storage等面板
- 设置断点调试

### 3. 真机调试

1. 点击"预览"生成二维码
2. 手机扫码打开小程序
3. 开启"调试模式"查看日志

## 总结

通过以上示例，您可以：
1. 创建新页面并集成API
2. 处理表单提交
3. 实现列表分页加载
4. 上传和预览图片
5. 应用最佳实践

更多详细信息，请参考：
- `miniprogram/README.md` - 前端文档
- `MINIPROGRAM_GUIDE.md` - 集成指南
- `API.md` - 后端API文档
