// pages/study/study.js - 学习页面
const resourceApi = require('../../api/resource.js');
const planApi = require('../../api/plan.js');

Page({
  data: {
    activeTab: 0,
    tabs: ['学习资源', '我的计划', '今日任务'],
    resources: [],
    plans: [],
    todayTasks: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad: function () {
    this.loadData();
  },

  onShow: function() {
    if (this.data.activeTab === 1) {
      this.loadPlans();
    }
  },

  /**
   * 切换标签
   */
  switchTab: function(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      activeTab: index,
      pageNum: 1,
      hasMore: true
    });
    this.loadData();
  },

  /**
   * 加载数据
   */
  loadData: function() {
    switch(this.data.activeTab) {
      case 0:
        this.loadResources();
        break;
      case 1:
        this.loadPlans();
        break;
      case 2:
        this.loadTodayTasks();
        break;
    }
  },

  /**
   * 加载学习资源
   */
  loadResources: function() {
    if (this.data.loading || !this.data.hasMore) return;

    this.setData({ loading: true });

    resourceApi.getResourceList({
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }).then(result => {
      const resources = this.data.pageNum === 1 ? result.list : [...this.data.resources, ...result.list];
      this.setData({
        resources: resources,
        loading: false,
        hasMore: result.hasNextPage
      });
    }).catch(err => {
      this.setData({ loading: false });
    });
  },

  /**
   * 加载学习计划
   */
  loadPlans: function() {
    this.setData({ loading: true });

    planApi.getMyPlans().then(plans => {
      this.setData({
        plans: plans,
        loading: false
      });
    }).catch(err => {
      this.setData({ loading: false });
    });
  },

  /**
   * 加载今日任务
   */
  loadTodayTasks: function() {
    this.setData({ loading: true });

    planApi.getActivePlans().then(plans => {
      if (plans && plans.length > 0) {
        // 获取第一个活跃计划的今日任务
        return planApi.getTodayPlan(plans[0].id);
      }
      return [];
    }).then(tasks => {
      this.setData({
        todayTasks: tasks,
        loading: false
      });
    }).catch(err => {
      this.setData({ loading: false });
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.setData({ pageNum: 1, hasMore: true });
    this.loadData();
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉加载更多
   */
  onReachBottom: function() {
    if (this.data.activeTab === 0 && this.data.hasMore) {
      this.setData({
        pageNum: this.data.pageNum + 1
      });
      this.loadResources();
    }
  },

  /**
   * 跳转到资源详情
   */
  goToResourceDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/resource/detail/detail?id=${id}`
    });
  },

  /**
   * 跳转到计划详情
   */
  goToPlanDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/plan/detail/detail?id=${id}`
    });
  },

  /**
   * 创建计划
   */
  createPlan: function() {
    wx.navigateTo({
      url: '/pages/plan/create/create'
    });
  },

  /**
   * 完成任务
   */
  completeTask: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '完成任务',
      content: '确认已完成此任务？',
      success: (res) => {
        if (res.confirm) {
          planApi.completePlanItem(id, 1).then(() => {
            wx.showToast({
              title: '已完成',
              icon: 'success'
            });
            this.loadTodayTasks();
          });
        }
      }
    });
  }
});
