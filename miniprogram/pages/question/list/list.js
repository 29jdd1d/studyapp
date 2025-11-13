// pages/question/list/list.js
const questionApi = require('../../../api/question.js');

Page({
  data: {
    subjects: ['全部', '政治', '英语', '数学', '专业课'],
    types: [
      { label: '全部', value: '' },
      { label: '单选', value: 'SINGLE_CHOICE' },
      { label: '多选', value: 'MULTIPLE_CHOICE' },
      { label: '判断', value: 'TRUE_FALSE' },
      { label: '简答', value: 'SHORT_ANSWER' }
    ],
    selectedSubject: '全部',
    selectedType: '',
    questions: [],
    loading: false,
    pageNum: 1,
    pageSize: 20,
    hasMore: true
  },

  onLoad(options) {
    // 从参数获取初始筛选条件
    if (options.subject) {
      this.setData({ selectedSubject: options.subject });
    }
    if (options.type) {
      this.setData({ selectedType: options.type });
    }
    
    this.loadQuestions();
  },

  /**
   * 加载题目列表
   */
  loadQuestions(reset = false) {
    if (this.data.loading) return;

    if (reset) {
      this.setData({
        pageNum: 1,
        questions: [],
        hasMore: true
      });
    }

    this.setData({ loading: true });

    const { selectedSubject, selectedType, pageNum, pageSize } = this.data;
    const params = {
      pageNum,
      pageSize,
      subject: selectedSubject === '全部' ? undefined : selectedSubject,
      type: selectedType || undefined
    };

    questionApi.getQuestionList(params)
      .then(result => {
        const newQuestions = reset ? result.list : [...this.data.questions, ...result.list];
        this.setData({
          questions: newQuestions,
          loading: false,
          hasMore: result.list.length >= pageSize
        });
      })
      .catch(err => {
        console.error('加载题目列表失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
      });
  },

  /**
   * 选择科目
   */
  selectSubject(e) {
    const { value } = e.currentTarget.dataset;
    this.setData({ selectedSubject: value });
    this.loadQuestions(true);
  },

  /**
   * 选择类型
   */
  selectType(e) {
    const { value } = e.currentTarget.dataset;
    this.setData({ selectedType: value });
    this.loadQuestions(true);
  },

  /**
   * 加载更多
   */
  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      pageNum: this.data.pageNum + 1
    });
    this.loadQuestions();
  },

  /**
   * 跳转到题目详情
   */
  goToQuestion(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/question/practice/practice?id=${id}`
    });
  },

  /**
   * 开始练习
   */
  startPractice() {
    const { selectedSubject } = this.data;
    const subject = selectedSubject === '全部' ? '' : selectedSubject;
    
    wx.navigateTo({
      url: `/pages/question/practice/practice?subject=${subject}&mode=practice`
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadQuestions(true);
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉加载
   */
  onReachBottom() {
    this.loadMore();
  }
});
