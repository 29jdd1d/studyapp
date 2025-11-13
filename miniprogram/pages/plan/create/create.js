// pages/plan/create/create.js
const planApi = require('../../../api/plan.js');

Page({
  data: {
    formData: {
      title: '',
      description: '',
      targetUniversity: '',
      targetMajor: '',
      examDate: ''
    },
    minDate: '',
    maxDate: '',
    loading: false,
    loadingText: '处理中...'
  },

  onLoad() {
    // 设置日期范围
    const today = new Date();
    const nextYear = new Date();
    nextYear.setFullYear(today.getFullYear() + 2);

    this.setData({
      minDate: this.formatDateForPicker(today),
      maxDate: this.formatDateForPicker(nextYear)
    });
  },

  /**
   * 格式化日期为 picker 格式
   */
  formatDateForPicker(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  },

  /**
   * 标题输入
   */
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    });
  },

  /**
   * 描述输入
   */
  onDescriptionInput(e) {
    this.setData({
      'formData.description': e.detail.value
    });
  },

  /**
   * 院校输入
   */
  onUniversityInput(e) {
    this.setData({
      'formData.targetUniversity': e.detail.value
    });
  },

  /**
   * 专业输入
   */
  onMajorInput(e) {
    this.setData({
      'formData.targetMajor': e.detail.value
    });
  },

  /**
   * 日期选择
   */
  onDateChange(e) {
    this.setData({
      'formData.examDate': e.detail.value
    });
  },

  /**
   * 生成推荐计划
   */
  generateRecommend() {
    const { targetUniversity, targetMajor, examDate } = this.data.formData;

    if (!targetUniversity || !targetMajor || !examDate) {
      wx.showToast({
        title: '请先填写目标院校、专业和考试日期',
        icon: 'none',
        duration: 2000
      });
      return;
    }

    this.setData({ 
      loading: true,
      loadingText: '正在生成推荐计划...' 
    });

    const params = {
      targetUniversity,
      targetMajor,
      examDate
    };

    planApi.generateRecommendPlan(params)
      .then(plan => {
        this.setData({ loading: false });
        
        wx.showToast({
          title: '推荐计划已生成',
          icon: 'success'
        });

        // 填充推荐计划的标题和描述
        if (plan.title) {
          this.setData({
            'formData.title': plan.title,
            'formData.description': plan.description || ''
          });
        }

        // 如果后端直接创建了计划，跳转到详情页
        if (plan.id) {
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/plan/detail/detail?id=${plan.id}`
            });
          }, 1000);
        }
      })
      .catch(err => {
        this.setData({ loading: false });
        console.error('生成推荐计划失败:', err);
        wx.showToast({
          title: '生成失败，请重试',
          icon: 'none'
        });
      });
  },

  /**
   * 提交表单
   */
  submit() {
    const { title, description, targetUniversity, targetMajor, examDate } = this.data.formData;

    // 验证必填项
    if (!title || !targetUniversity || !targetMajor || !examDate) {
      wx.showToast({
        title: '请填写所有必填项',
        icon: 'none'
      });
      return;
    }

    this.setData({ 
      loading: true,
      loadingText: '创建中...' 
    });

    const planData = {
      title,
      description,
      targetUniversity,
      targetMajor,
      examDate
    };

    planApi.createPlan(planData)
      .then(plan => {
        this.setData({ loading: false });
        
        wx.showToast({
          title: '创建成功',
          icon: 'success'
        });

        // 跳转到计划详情页
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/plan/detail/detail?id=${plan.id}`
          });
        }, 1000);
      })
      .catch(err => {
        this.setData({ loading: false });
        console.error('创建计划失败:', err);
        wx.showToast({
          title: '创建失败，请重试',
          icon: 'none'
        });
      });
  },

  /**
   * 取消
   */
  cancel() {
    wx.navigateBack();
  }
});
