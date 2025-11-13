// pages/question/practice/practice.js
const questionApi = require('../../../api/question.js');

Page({
  data: {
    questions: [],
    currentIndex: 0,
    currentQuestion: null,
    selectedAnswer: '',
    answerText: '',
    showAnswer: false,
    isCorrect: false,
    answeredCount: 0,
    correctCount: 0,
    totalQuestions: 0,
    loading: true,
    error: null,
    startTime: 0
  },

  onLoad(options) {
    const { id, subject, mode } = options;
    
    if (id) {
      // 单题模式
      this.loadSingleQuestion(id);
    } else if (subject || mode === 'practice') {
      // 练习模式
      this.loadPracticeQuestions(subject);
    } else {
      this.setData({
        loading: false,
        error: '参数错误'
      });
    }
  },

  /**
   * 加载单个题目
   */
  loadSingleQuestion(id) {
    this.setData({ loading: true, error: null });

    questionApi.getQuestionDetail(id)
      .then(question => {
        question.options = this.parseOptions(question);
        this.setData({
          questions: [question],
          currentQuestion: question,
          totalQuestions: 1,
          loading: false,
          startTime: Date.now()
        });
      })
      .catch(err => {
        console.error('加载题目失败:', err);
        this.setData({
          loading: false,
          error: '加载失败，请重试'
        });
      });
  },

  /**
   * 加载练习题目
   */
  loadPracticeQuestions(subject) {
    this.setData({ loading: true, error: null });

    questionApi.getSmartPractice(subject, 20)
      .then(questions => {
        questions = questions.map(q => {
          q.options = this.parseOptions(q);
          return q;
        });
        
        this.setData({
          questions,
          currentQuestion: questions[0],
          totalQuestions: questions.length,
          loading: false,
          startTime: Date.now()
        });
      })
      .catch(err => {
        console.error('加载练习题目失败:', err);
        this.setData({
          loading: false,
          error: '加载失败，请重试'
        });
      });
  },

  /**
   * 解析选项
   */
  parseOptions(question) {
    if (question.type === 'SHORT_ANSWER') {
      return [];
    }

    if (question.type === 'TRUE_FALSE') {
      return [
        { key: 'A', value: '正确' },
        { key: 'B', value: '错误' }
      ];
    }

    // 解析选择题选项 (格式: A.选项1;B.选项2;C.选项3;D.选项4)
    const optionsStr = question.options || '';
    const options = [];
    const parts = optionsStr.split(';');
    
    parts.forEach(part => {
      const match = part.match(/^([A-Z])\.(.*)/);
      if (match) {
        options.push({
          key: match[1],
          value: match[2].trim()
        });
      }
    });

    return options;
  },

  /**
   * 选择选项
   */
  selectOption(e) {
    if (this.data.showAnswer) return;

    const { key } = e.currentTarget.dataset;
    const { currentQuestion } = this.data;

    if (currentQuestion.type === 'MULTIPLE_CHOICE') {
      // 多选题
      let selected = this.data.selectedAnswer.split('');
      const index = selected.indexOf(key);
      
      if (index > -1) {
        selected.splice(index, 1);
      } else {
        selected.push(key);
      }
      
      selected.sort();
      this.setData({ selectedAnswer: selected.join('') });
    } else {
      // 单选题/判断题
      this.setData({ selectedAnswer: key });
    }
  },

  /**
   * 答案输入
   */
  onAnswerInput(e) {
    this.setData({ answerText: e.detail.value });
  },

  /**
   * 提交答案
   */
  submitAnswer() {
    const { currentQuestion, selectedAnswer, answerText } = this.data;
    const answer = currentQuestion.type === 'SHORT_ANSWER' ? answerText : selectedAnswer;

    if (!answer) {
      wx.showToast({
        title: '请先作答',
        icon: 'none'
      });
      return;
    }

    // 计算答题时间
    const timeSpent = Math.floor((Date.now() - this.data.startTime) / 1000);

    // 提交答案到服务器
    questionApi.submitAnswer(currentQuestion.id, answer, timeSpent)
      .then(result => {
        const isCorrect = result.correct || false;
        
        this.setData({
          showAnswer: true,
          isCorrect,
          answeredCount: this.data.answeredCount + 1,
          correctCount: this.data.correctCount + (isCorrect ? 1 : 0)
        });
      })
      .catch(err => {
        console.error('提交答案失败:', err);
        // 即使提交失败，也显示本地判断结果
        const isCorrect = this.checkAnswer(answer, currentQuestion.correctAnswer);
        this.setData({
          showAnswer: true,
          isCorrect,
          answeredCount: this.data.answeredCount + 1,
          correctCount: this.data.correctCount + (isCorrect ? 1 : 0)
        });
      });
  },

  /**
   * 本地检查答案
   */
  checkAnswer(userAnswer, correctAnswer) {
    return userAnswer.trim().toUpperCase() === correctAnswer.trim().toUpperCase();
  },

  /**
   * 下一题
   */
  nextQuestion() {
    const { currentIndex, questions } = this.data;
    
    if (currentIndex >= questions.length - 1) {
      return;
    }

    const nextIndex = currentIndex + 1;
    this.setData({
      currentIndex: nextIndex,
      currentQuestion: questions[nextIndex],
      selectedAnswer: '',
      answerText: '',
      showAnswer: false,
      isCorrect: false,
      startTime: Date.now()
    });
  },

  /**
   * 上一题
   */
  prevQuestion() {
    const { currentIndex, questions } = this.data;
    
    if (currentIndex <= 0) {
      return;
    }

    const prevIndex = currentIndex - 1;
    this.setData({
      currentIndex: prevIndex,
      currentQuestion: questions[prevIndex],
      selectedAnswer: '',
      answerText: '',
      showAnswer: false,
      isCorrect: false,
      startTime: Date.now()
    });
  },

  /**
   * 完成练习
   */
  finishPractice() {
    const { totalQuestions, correctCount } = this.data;
    const accuracy = Math.round(correctCount / totalQuestions * 100);

    wx.showModal({
      title: '练习完成',
      content: `本次练习完成 ${totalQuestions} 题，正确 ${correctCount} 题，正确率 ${accuracy}%`,
      confirmText: '返回',
      showCancel: false,
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack();
        }
      }
    });
  },

  /**
   * 重试
   */
  retry() {
    this.onLoad(this.options || {});
  }
});
