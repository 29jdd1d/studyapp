// pages/index/index.js - 首页
const userApi = require('../../api/user.js');
const communityApi = require('../../api/community.js');
const app = getApp();

Page({
  data: {
    userInfo: null,
    dashboard: null,
    pinnedPosts: [],
    loading: true
  },

  onLoad: function () {
    this.checkLogin();
  },

  onShow: function() {
    if (app.isLoggedIn()) {
      this.loadData();
    }
  },

  /**
   * 检查登录状态
   */
  checkLogin: function() {
    if (!app.isLoggedIn()) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
    } else {
      this.loadData();
    }
  },

  /**
   * 加载数据
   */
  loadData: function() {
    this.setData({ loading: true });

    Promise.all([
      userApi.getDashboard(),
      communityApi.getPinnedPosts()
    ]).then(results => {
      this.setData({
        userInfo: app.getUserInfo(),
        dashboard: results[0],
        pinnedPosts: results[1].slice(0, 3), // 只显示前3条
        loading: false
      });
    }).catch(err => {
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
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
   * 跳转到学习资源
   */
  goToResource: function() {
    wx.navigateTo({
      url: '/pages/resource/list/list'
    });
  },

  /**
   * 跳转到学习计划
   */
  goToPlan: function() {
    wx.navigateTo({
      url: '/pages/plan/list/list'
    });
  },

  /**
   * 跳转到刷题
   */
  goToQuestion: function() {
    wx.navigateTo({
      url: '/pages/question/list/list'
    });
  },

  /**
   * 跳转到打卡
   */
  goToCheckIn: function() {
    wx.navigateTo({
      url: '/pages/community/checkin/checkin'
    });
  },

  /**
   * 跳转到帖子详情
   */
  goToPostDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/community/post/detail/detail?id=${id}`
    });
  }
});
