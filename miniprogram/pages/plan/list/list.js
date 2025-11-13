// pages/plan/list/list.js
const planApi = require('../../../api/plan.js');

Page({
  data: {
    plans: [],
    loading: false
  },

  onLoad() {
    this.loadPlans();
  },

  /**
   * 加载学习计划列表
   */
  loadPlans() {
    this.setData({ loading: true });

    planApi.getMyPlans()
      .then(plans => {
        // 格式化日期
        plans = plans.map(plan => {
          plan.createTime = this.formatDate(plan.createTime);
          plan.examDate = this.formatDate(plan.examDate);
          return plan;
        });

        this.setData({
          plans,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载学习计划失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  /**
   * 格式化日期
   */
  formatDate(dateStr) {
    if (!dateStr) return '';
    
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
  },

  /**
   * 跳转到计划详情
   */
  goToPlanDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/plan/detail/detail?id=${id}`
    });
  },

  /**
   * 创建计划
   */
  createPlan() {
    wx.navigateTo({
      url: '/pages/plan/create/create'
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadPlans();
    wx.stopPullDownRefresh();
  },

  /**
   * 页面显示时刷新
   */
  onShow() {
    // 从创建页面返回时刷新列表
    if (this.data.plans.length > 0) {
      this.loadPlans();
    }
  }
});
