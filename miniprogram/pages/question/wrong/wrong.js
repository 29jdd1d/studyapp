// pages/question/wrong/wrong.js
const questionApi = require('../../../api/question.js');

Page({
  data: {
    activeTab: 0,
    wrongQuestions: [],
    loading: false
  },

  onLoad() {
    this.loadWrongQuestions();
  },

  /**
   * 切换标签
   */
  switchTab(e) {
    const { index } = e.currentTarget.dataset;
    this.setData({ activeTab: index });
    this.loadWrongQuestions();
  },

  /**
   * 加载错题列表
   */
  loadWrongQuestions() {
    this.setData({ loading: true });

    const mastered = this.data.activeTab === 1;

    questionApi.getWrongQuestions(mastered)
      .then(wrongQuestions => {
        this.setData({
          wrongQuestions,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载错题列表失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  /**
   * 标记已掌握
   */
  markMastered(e) {
    const { id } = e.currentTarget.dataset;

    wx.showModal({
      title: '确认',
      content: '确定要标记这道题为已掌握吗？',
      success: (res) => {
        if (res.confirm) {
          this.doMarkMastered(id);
        }
      }
    });
  },

  /**
   * 执行标记已掌握
   */
  doMarkMastered(questionId) {
    wx.showLoading({ title: '处理中...' });

    questionApi.markWrongQuestionMastered(questionId)
      .then(() => {
        wx.hideLoading();
        wx.showToast({
          title: '已标记为已掌握',
          icon: 'success'
        });
        // 重新加载列表
        this.loadWrongQuestions();
      })
      .catch(err => {
        wx.hideLoading();
        console.error('标记已掌握失败:', err);
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        });
      });
  },

  /**
   * 跳转到练习页面
   */
  goToPractice(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/question/practice/practice?id=${id}`
    });
  },

  /**
   * 开始练习错题
   */
  startPractice() {
    // 使用第一道错题开始练习
    if (this.data.wrongQuestions.length > 0) {
      const firstQuestionId = this.data.wrongQuestions[0].questionId;
      wx.navigateTo({
        url: `/pages/question/practice/practice?id=${firstQuestionId}`
      });
    }
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadWrongQuestions();
    wx.stopPullDownRefresh();
  }
});
