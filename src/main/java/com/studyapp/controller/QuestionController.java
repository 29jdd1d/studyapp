package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.entity.Question;
import com.studyapp.service.QuestionService;
import com.studyapp.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库控制器
 * Question Bank Controller
 */
@Api(tags = "题库与刷题")
@RestController
@RequestMapping("/question")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("创建题目")
    @PostMapping
    public Result<Question> createQuestion(@RequestBody Question question) {
        Question created = questionService.createQuestion(question);
        return Result.success(created);
    }
    
    @ApiOperation("分页查询题目")
    @GetMapping("/list")
    public Result<Page<Question>> getQuestions(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String chapter,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Question> page = questionService.getQuestions(subject, chapter, type, year, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取题目详情")
    @GetMapping("/{id}")
    public Result<Question> getQuestion(@PathVariable Long id) {
        Question question = questionService.getQuestion(id);
        return Result.success(question);
    }
    
    @ApiOperation("提交答案")
    @PostMapping("/{id}/answer")
    public Result<Boolean> submitAnswer(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam String answer,
            @RequestParam(required = false) Integer timeSpent) {
        Long userId = getUserIdFromRequest(request);
        boolean isCorrect = questionService.submitAnswer(userId, id, answer, timeSpent);
        return Result.success(isCorrect);
    }
    
    @ApiOperation("获取错题列表")
    @GetMapping("/wrong")
    public Result<List<Question>> getWrongQuestions(
            HttpServletRequest request,
            @RequestParam(required = false) Boolean mastered) {
        Long userId = getUserIdFromRequest(request);
        List<Question> questions = questionService.getWrongQuestions(userId, mastered);
        return Result.success(questions);
    }
    
    @ApiOperation("标记错题已掌握")
    @PostMapping("/wrong/{questionId}/master")
    public Result<Void> markWrongQuestionMastered(
            HttpServletRequest request,
            @PathVariable Long questionId) {
        Long userId = getUserIdFromRequest(request);
        questionService.markWrongQuestionMastered(userId, questionId);
        return Result.success();
    }
    
    @ApiOperation("获取智能练习题目")
    @GetMapping("/smart-practice")
    public Result<List<Question>> getSmartPracticeQuestions(
            HttpServletRequest request,
            @RequestParam String subject,
            @RequestParam(defaultValue = "20") Integer count) {
        Long userId = getUserIdFromRequest(request);
        List<Question> questions = questionService.getSmartPracticeQuestions(userId, subject, count);
        return Result.success(questions);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
