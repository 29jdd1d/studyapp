// app.js - 小程序主入口文件
App({
  // 全局数据
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/api'
  },

  // 小程序初始化
  onLaunch: function () {
    console.log('小程序启动');
    
    // 检查是否已登录
    this.checkLogin();
    
    // 检查更新
    this.checkUpdate();
  },

  // 检查登录状态
  checkLogin: function() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = wx.getStorageSync('userInfo');
    }
  },

  // 检查小程序更新
  checkUpdate: function() {
    const updateManager = wx.getUpdateManager();
    
    updateManager.onCheckForUpdate(function (res) {
      // 请求完新版本信息的回调
      if (res.hasUpdate) {
        console.log('发现新版本');
      }
    });

    updateManager.onUpdateReady(function () {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success: function (res) {
          if (res.confirm) {
            updateManager.applyUpdate();
          }
        }
      });
    });

    updateManager.onUpdateFailed(function () {
      console.log('新版本下载失败');
    });
  },

  // 保存登录信息
  saveLoginInfo: function(token, userInfo) {
    this.globalData.token = token;
    this.globalData.userInfo = userInfo;
    wx.setStorageSync('token', token);
    wx.setStorageSync('userInfo', userInfo);
  },

  // 清除登录信息
  clearLoginInfo: function() {
    this.globalData.token = null;
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  // 获取用户信息
  getUserInfo: function() {
    return this.globalData.userInfo;
  },

  // 获取token
  getToken: function() {
    return this.globalData.token;
  },

  // 检查是否已登录
  isLoggedIn: function() {
    return !!this.globalData.token;
  }
});
