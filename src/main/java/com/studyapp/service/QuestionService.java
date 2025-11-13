package com.studyapp.service;

import com.studyapp.entity.AnswerRecord;
import com.studyapp.entity.Question;
import com.studyapp.entity.WrongQuestion;
import com.studyapp.repository.AnswerRecordRepository;
import com.studyapp.repository.QuestionRepository;
import com.studyapp.repository.WrongQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题库服务类
 * Question Bank Service
 */
@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private AnswerRecordRepository answerRecordRepository;
    
    @Autowired
    private WrongQuestionRepository wrongQuestionRepository;
    
    /**
     * 创建题目
     */
    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * 分页查询题目（使用Redis缓存）
     */
    @Cacheable(value = "questions", key = "#subject + '_' + #chapter + '_' + #type + '_' + #year + '_' + #pageNum + '_' + #pageSize")
    public Page<Question> getQuestions(String subject, String chapter, String type, String year, 
                                       Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        if (year != null && !year.isEmpty()) {
            return questionRepository.findByYear(year, pageable);
        } else if (subject != null && chapter != null) {
            return questionRepository.findBySubjectAndChapter(subject, chapter, pageable);
        } else if (subject != null && type != null) {
            return questionRepository.findBySubjectAndType(subject, type, pageable);
        } else if (subject != null) {
            return questionRepository.findBySubject(subject, pageable);
        } else {
            return questionRepository.findAll(pageable);
        }
    }
    
    /**
     * 分页查询题目（管理员用，支持按内容搜索）
     */
    @Cacheable(value = "admin_questions", key = "#content + '_' + #type + '_' + #pageNum + '_' + #pageSize")
    public Page<Question> getQuestionsForAdmin(String content, String type, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        if (content != null && !content.isEmpty() && type != null && !type.isEmpty()) {
            return questionRepository.findByContentContainingAndType(content, type, pageable);
        } else if (content != null && !content.isEmpty()) {
            return questionRepository.findByContentContaining(content, pageable);
        } else if (type != null && !type.isEmpty()) {
            return questionRepository.findByType(type, pageable);
        } else {
            return questionRepository.findAll(pageable);
        }
    }
    
    /**
     * 获取题目详情（使用Redis缓存）
     */
    @Cacheable(value = "question", key = "#id")
    public Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
    }
    
    /**
     * 提交答案
     */
    @Transactional
    public boolean submitAnswer(Long userId, Long questionId, String userAnswer, Integer timeSpent) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        
        // 判断答案是否正确
        boolean isCorrect = question.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
        
        // 保存答题记录
        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        record.setIsCorrect(isCorrect);
        record.setTimeSpent(timeSpent);
        answerRecordRepository.save(record);
        
        // 更新题目统计
        question.setAnswerCount(question.getAnswerCount() + 1);
        if (isCorrect) {
            question.setCorrectCount(question.getCorrectCount() + 1);
        }
        questionRepository.save(question);
        
        // 如果答错，添加到错题本
        if (!isCorrect) {
            addToWrongQuestions(userId, questionId);
        }
        
        return isCorrect;
    }
    
    /**
     * 添加到错题本
     */
    @Transactional
    public void addToWrongQuestions(Long userId, Long questionId) {
        WrongQuestion wrongQuestion = wrongQuestionRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .orElse(null);
        
        if (wrongQuestion == null) {
            wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(userId);
            wrongQuestion.setQuestionId(questionId);
            wrongQuestion.setWrongCount(1);
        } else {
            wrongQuestion.setWrongCount(wrongQuestion.getWrongCount() + 1);
        }
        
        wrongQuestion.setLastReviewTime(LocalDateTime.now());
        wrongQuestionRepository.save(wrongQuestion);
    }
    
    /**
     * 获取错题列表
     */
    public List<Question> getWrongQuestions(Long userId, Boolean mastered) {
        List<WrongQuestion> wrongQuestions;
        if (mastered != null) {
            wrongQuestions = wrongQuestionRepository.findByUserIdAndMastered(userId, mastered);
        } else {
            wrongQuestions = wrongQuestionRepository.findByUserId(userId);
        }
        
        List<Long> questionIds = wrongQuestions.stream()
                .map(WrongQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        return questionRepository.findAllById(questionIds);
    }
    
    /**
     * 标记错题已掌握
     */
    @Transactional
    public void markWrongQuestionMastered(Long userId, Long questionId) {
        WrongQuestion wrongQuestion = wrongQuestionRepository
                .findByUserIdAndQuestionId(userId, questionId)
                .orElseThrow(() -> new RuntimeException("错题记录不存在"));
        
        wrongQuestion.setMastered(true);
        wrongQuestionRepository.save(wrongQuestion);
    }
    
    /**
     * 获取智能练习题目（错题优先）
     */
    public List<Question> getSmartPracticeQuestions(Long userId, String subject, Integer count) {
        // 先获取未掌握的错题
        List<WrongQuestion> wrongQuestions = wrongQuestionRepository
                .findByUserIdAndMastered(userId, false);
        
        List<Long> wrongQuestionIds = wrongQuestions.stream()
                .map(WrongQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        List<Question> questions = questionRepository.findAllById(wrongQuestionIds);
        
        // 如果错题不够，再从题库中随机获取
        if (questions.size() < count) {
            Pageable pageable = PageRequest.of(0, count - questions.size());
            Page<Question> additionalQuestions = questionRepository.findBySubject(subject, pageable);
            questions.addAll(additionalQuestions.getContent());
        }
        
        return questions;
    }
}
