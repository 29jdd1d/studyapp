// config.js - 配置文件
module.exports = {
  // API基础地址
  baseUrl: 'http://localhost:8080/api',
  
  // 微信小程序配置
  wechat: {
    // 需要在微信公众平台配置
    appId: 'your_wechat_appid_here'
  },
  
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // 分页配置
  pagination: {
    defaultPageSize: 10,
    maxPageSize: 100
  },
  
  // 文件上传配置
  upload: {
    // 图片最大大小（字节）
    maxImageSize: 10 * 1024 * 1024, // 10MB
    // 视频最大大小（字节）
    maxVideoSize: 100 * 1024 * 1024, // 100MB
    // 文档最大大小（字节）
    maxDocumentSize: 50 * 1024 * 1024 // 50MB
  }
};
