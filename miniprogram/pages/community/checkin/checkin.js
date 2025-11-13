// pages/community/checkin/checkin.js
const communityApi = require('../../../api/community.js');

Page({
  data: {
    continuousDays: 0,
    totalDays: 0,
    thisMonthDays: 0,
    studyHours: 0,
    todayChecked: false,
    todayCheckInTime: '',
    todayNote: '',
    currentMonth: '',
    weekdays: ['日', '一', '二', '三', '四', '五', '六'],
    calendarDays: [],
    records: [],
    loading: false,
    showDialog: false,
    studyHoursInput: '',
    noteInput: ''
  },

  onLoad() {
    this.initCalendar();
    this.loadCheckInData();
  },

  /**
   * 初始化日历
   */
  initCalendar() {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth();
    
    this.setData({
      currentMonth: `${year}年${month + 1}月`
    });

    // 生成日历数据
    const calendarDays = this.generateCalendarDays(year, month);
    this.setData({ calendarDays });
  },

  /**
   * 生成日历天数
   */
  generateCalendarDays(year, month) {
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startWeekday = firstDay.getDay();

    const days = [];
    const today = new Date();
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

    // 补充上月天数
    for (let i = 0; i < startWeekday; i++) {
      days.push({ day: '', inMonth: false, checked: false });
    }

    // 当月天数
    for (let i = 1; i <= daysInMonth; i++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
      days.push({
        day: i,
        inMonth: true,
        isToday: dateStr === todayStr,
        checked: false,
        date: dateStr
      });
    }

    // 补充下月天数
    const remainingDays = 42 - days.length;
    for (let i = 0; i < remainingDays; i++) {
      days.push({ day: '', inMonth: false, checked: false });
    }

    return days;
  },

  /**
   * 加载打卡数据
   */
  loadCheckInData() {
    this.setData({ loading: true });

    Promise.all([
      communityApi.getContinuousDays(),
      communityApi.getCheckInRecords()
    ])
      .then(([continuousData, records]) => {
        // 处理连续打卡数据
        this.setData({
          continuousDays: continuousData.continuousDays || 0,
          loading: false
        });

        // 处理打卡记录
        this.processCheckInRecords(records);
      })
      .catch(err => {
        console.error('加载打卡数据失败:', err);
        this.setData({ loading: false });
      });
  },

  /**
   * 处理打卡记录
   */
  processCheckInRecords(records) {
    let totalDays = 0;
    let thisMonthDays = 0;
    let totalStudyHours = 0;
    const today = new Date();
    const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
    const thisMonth = today.getMonth() + 1;

    const formattedRecords = records.map(record => {
      const date = new Date(record.checkInDate);
      const dateStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;

      totalDays++;
      totalStudyHours += record.studyHours || 0;

      if (date.getMonth() + 1 === thisMonth) {
        thisMonthDays++;
      }

      // 检查今天是否已打卡
      if (dateStr === todayStr) {
        this.setData({
          todayChecked: true,
          todayCheckInTime: this.formatTime(record.createTime),
          todayNote: record.note || ''
        });
      }

      // 更新日历标记
      this.markCalendarDay(dateStr);

      return {
        id: record.id,
        day: date.getDate(),
        month: `${date.getMonth() + 1}月`,
        time: this.formatTime(record.createTime),
        studyHours: record.studyHours || 0,
        note: record.note || ''
      };
    });

    this.setData({
      totalDays,
      thisMonthDays,
      studyHours: totalStudyHours,
      records: formattedRecords
    });
  },

  /**
   * 标记日历中的打卡日期
   */
  markCalendarDay(dateStr) {
    const calendarDays = this.data.calendarDays;
    const day = calendarDays.find(d => d.date === dateStr);
    if (day) {
      day.checked = true;
      this.setData({ calendarDays });
    }
  },

  /**
   * 格式化时间
   */
  formatTime(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  },

  /**
   * 显示打卡对话框
   */
  showCheckInDialog() {
    if (this.data.todayChecked) {
      wx.showToast({
        title: '今天已经打卡了',
        icon: 'none'
      });
      return;
    }

    this.setData({
      showDialog: true,
      studyHoursInput: '',
      noteInput: ''
    });
  },

  /**
   * 隐藏打卡对话框
   */
  hideCheckInDialog() {
    this.setData({ showDialog: false });
  },

  /**
   * 阻止冒泡
   */
  stopPropagation() {},

  /**
   * 学习时长输入
   */
  onStudyHoursInput(e) {
    this.setData({
      studyHoursInput: e.detail.value
    });
  },

  /**
   * 笔记输入
   */
  onNoteInput(e) {
    this.setData({
      noteInput: e.detail.value
    });
  },

  /**
   * 提交打卡
   */
  submitCheckIn() {
    const { studyHoursInput, noteInput } = this.data;

    if (!studyHoursInput || parseFloat(studyHoursInput) <= 0) {
      wx.showToast({
        title: '请输入有效的学习时长',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    const checkInData = {
      studyHours: parseFloat(studyHoursInput),
      note: noteInput.trim()
    };

    communityApi.checkIn(checkInData)
      .then(() => {
        wx.hideLoading();
        wx.showToast({
          title: '打卡成功',
          icon: 'success'
        });

        this.setData({ showDialog: false });

        // 重新加载打卡数据
        setTimeout(() => {
          this.loadCheckInData();
        }, 1000);
      })
      .catch(err => {
        wx.hideLoading();
        console.error('打卡失败:', err);
        wx.showToast({
          title: '打卡失败',
          icon: 'none'
        });
      });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadCheckInData();
    wx.stopPullDownRefresh();
  }
});
