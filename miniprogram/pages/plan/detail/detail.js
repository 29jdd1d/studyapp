// pages/plan/detail/detail.js
const planApi = require('../../../api/plan.js');

Page({
  data: {
    plan: null,
    planItems: [],
    loading: true,
    error: null
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.planId = id;
      this.loadPlanDetail();
    } else {
      this.setData({
        loading: false,
        error: '计划ID不存在'
      });
    }
  },

  /**
   * 加载计划详情
   */
  loadPlanDetail() {
    this.setData({ loading: true, error: null });

    // 注意：这里假设后端有获取计划详情的接口
    // 如果没有，需要从计划列表中获取
    planApi.getMyPlans()
      .then(plans => {
        const plan = plans.find(p => p.id == this.planId);
        
        if (!plan) {
          this.setData({
            loading: false,
            error: '计划不存在'
          });
          return;
        }

        // 格式化日期
        plan.examDate = this.formatDate(plan.examDate);
        
        this.setData({
          plan,
          loading: false
        });

        // 加载计划项
        this.loadPlanItems();
      })
      .catch(err => {
        console.error('加载计划详情失败:', err);
        this.setData({
          loading: false,
          error: '加载失败，请重试'
        });
      });
  },

  /**
   * 加载计划项
   */
  loadPlanItems() {
    planApi.getTodayPlan(this.planId)
      .then(items => {
        // 格式化计划项
        items = items.map(item => {
          item.planDate = this.formatDate(item.planDate);
          return item;
        });

        this.setData({ planItems: items });
      })
      .catch(err => {
        console.error('加载计划项失败:', err);
        // 不影响主流程，只是没有计划项
      });
  },

  /**
   * 格式化日期
   */
  formatDate(dateStr) {
    if (!dateStr) return '';
    
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${month}-${day}`;
  },

  /**
   * 完成计划项
   */
  completeItem(e) {
    const { id } = e.currentTarget.dataset;

    wx.showModal({
      title: '完成任务',
      content: '确认完成这个学习任务吗？',
      success: (res) => {
        if (res.confirm) {
          this.doCompleteItem(id);
        }
      }
    });
  },

  /**
   * 执行完成计划项
   */
  doCompleteItem(itemId) {
    wx.showLoading({ title: '提交中...' });

    // 假设实际学习时长等于预估时长
    const item = this.data.planItems.find(i => i.id === itemId);
    const actualHours = item ? item.estimatedHours : 1;

    planApi.completePlanItem(itemId, actualHours)
      .then(() => {
        wx.hideLoading();
        wx.showToast({
          title: '任务已完成',
          icon: 'success'
        });

        // 更新本地状态
        const planItems = this.data.planItems.map(item => {
          if (item.id === itemId) {
            item.completed = true;
          }
          return item;
        });

        this.setData({ planItems });

        // 重新加载计划详情以更新进度
        setTimeout(() => {
          this.loadPlanDetail();
        }, 1000);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('完成计划项失败:', err);
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        });
      });
  },

  /**
   * 重试
   */
  retry() {
    this.loadPlanDetail();
  }
});
