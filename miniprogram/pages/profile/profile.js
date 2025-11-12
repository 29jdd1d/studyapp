// pages/profile/profile.js - 我的页面
const userApi = require('../../api/user.js');
const communityApi = require('../../api/community.js');
const app = getApp();

Page({
  data: {
    userInfo: null,
    dashboard: null,
    continuousDays: 0,
    menuList: [
      {
        title: '我的帖子',
        icon: '/images/icon-post.png',
        url: '/pages/community/post/list/list'
      },
      {
        title: '错题本',
        icon: '/images/icon-wrong.png',
        url: '/pages/question/wrong/wrong'
      },
      {
        title: '打卡记录',
        icon: '/images/icon-record.png',
        url: '/pages/community/checkin/checkin'
      },
      {
        title: '设置',
        icon: '/images/icon-setting.png',
        url: ''
      }
    ]
  },

  onLoad: function () {
    this.loadData();
  },

  onShow: function() {
    this.loadData();
  },

  /**
   * 加载数据
   */
  loadData: function() {
    if (!app.isLoggedIn()) {
      return;
    }

    Promise.all([
      userApi.getDashboard(),
      communityApi.getContinuousDays()
    ]).then(results => {
      this.setData({
        userInfo: app.getUserInfo(),
        dashboard: results[0],
        continuousDays: results[1]
      });
    }).catch(err => {
      console.error('加载数据失败', err);
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.loadData();
    wx.stopPullDownRefresh();
  },

  /**
   * 菜单点击
   */
  onMenuClick: function(e) {
    const url = e.currentTarget.dataset.url;
    if (url) {
      wx.navigateTo({ url });
    } else {
      wx.showToast({
        title: '功能开发中',
        icon: 'none'
      });
    }
  },

  /**
   * 退出登录
   */
  logout: function() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearLoginInfo();
          wx.reLaunch({
            url: '/pages/login/login'
          });
        }
      }
    });
  }
});
