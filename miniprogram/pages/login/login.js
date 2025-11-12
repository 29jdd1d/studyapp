// pages/login/login.js - 登录页面
const userApi = require('../../api/user.js');
const app = getApp();

Page({
  data: {
    canLogin: false
  },

  onLoad: function () {
    // 检查是否已登录
    if (app.isLoggedIn()) {
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  /**
   * 获取用户信息并登录
   */
  getUserProfile: function() {
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: (res) => {
        this.setData({
          canLogin: true
        });
        this.doLogin(res.userInfo);
      },
      fail: (err) => {
        wx.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 执行登录
   */
  doLogin: function(userInfo) {
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    // 获取微信登录凭证
    wx.login({
      success: (res) => {
        if (res.code) {
          // 调用后端登录接口
          const loginData = {
            code: res.code,
            nickName: userInfo.nickName,
            avatarUrl: userInfo.avatarUrl,
            gender: userInfo.gender
          };

          userApi.wechatLogin(loginData)
            .then(result => {
              wx.hideLoading();
              // 保存登录信息
              app.saveLoginInfo(result.token, result.user);
              
              wx.showToast({
                title: '登录成功',
                icon: 'success',
                duration: 1500
              });

              // 跳转到首页
              setTimeout(() => {
                wx.switchTab({
                  url: '/pages/index/index'
                });
              }, 1500);
            })
            .catch(err => {
              wx.hideLoading();
              wx.showToast({
                title: '登录失败',
                icon: 'none'
              });
            });
        } else {
          wx.hideLoading();
          wx.showToast({
            title: '获取登录凭证失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        });
      }
    });
  }
});
