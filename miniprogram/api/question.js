// api/question.js - 题库相关API
const { get, post } = require('../utils/request.js');

/**
 * 创建题目
 * @param {Object} data 题目数据
 */
function createQuestion(data) {
  return post('/question', data);
}

/**
 * 分页查询题目
 * @param {Object} params 查询参数
 */
function getQuestionList(params) {
  return get('/question/list', params);
}

/**
 * 获取题目详情
 * @param {Number} id 题目ID
 */
function getQuestionDetail(id) {
  return get(`/question/${id}`);
}

/**
 * 提交答案
 * @param {Number} id 题目ID
 * @param {String} answer 答案
 * @param {Number} timeSpent 答题时间（秒）
 */
function submitAnswer(id, answer, timeSpent) {
  // Backend expects @RequestParam, so we send as query parameters
  return post(`/question/${id}/answer?answer=${encodeURIComponent(answer)}${timeSpent ? '&timeSpent=' + timeSpent : ''}`);
}

/**
 * 获取错题列表
 * @param {Boolean} mastered 是否已掌握
 */
function getWrongQuestions(mastered) {
  return get('/question/wrong', { mastered });
}

/**
 * 标记错题已掌握
 * @param {Number} questionId 题目ID
 */
function markWrongQuestionMastered(questionId) {
  return post(`/question/wrong/${questionId}/master`);
}

/**
 * 获取智能练习题目
 * @param {String} subject 科目
 * @param {Number} count 题目数量
 */
function getSmartPractice(subject, count = 20) {
  return get('/question/smart-practice', { subject, count });
}

module.exports = {
  createQuestion,
  getQuestionList,
  getQuestionDetail,
  submitAnswer,
  getWrongQuestions,
  markWrongQuestionMastered,
  getSmartPractice
};
