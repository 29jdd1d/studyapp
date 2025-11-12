package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.entity.*;
import com.studyapp.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理控制器
 * Admin Controller
 */
@Api(tags = "系统后台管理")
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LearningResourceService resourceService;
    
    @Autowired
    private StudyPlanService planService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private CommunityService communityService;
    
    @ApiOperation("获取系统统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以添加各种统计数据
        stats.put("totalUsers", "统计数据");
        stats.put("totalResources", "统计数据");
        stats.put("totalQuestions", "统计数据");
        stats.put("totalPosts", "统计数据");
        
        return Result.success(stats);
    }
    
    @ApiOperation("获取所有资源（管理员）")
    @GetMapping("/resources")
    public Result<Page<LearningResource>> getAllResources(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<LearningResource> page = resourceService.getResources(subject, type, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("发布资源")
    @PostMapping("/resource/{id}/publish")
    public Result<Void> publishResource(@PathVariable Long id) {
        resourceService.publishResource(id);
        return Result.success();
    }
    
    @ApiOperation("删除资源")
    @DeleteMapping("/resource/{id}")
    public Result<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.success();
    }
    
    @ApiOperation("获取所有题目（管理员）")
    @GetMapping("/questions")
    public Result<Page<Question>> getAllQuestions(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String chapter,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Question> page = questionService.getQuestions(subject, chapter, type, year, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("删除题目")
    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.createQuestion(null); // 需要实现删除方法
        return Result.success();
    }
    
    @ApiOperation("删除帖子（管理员）")
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return Result.success();
    }
    
    @ApiOperation("获取所有帖子（管理员）")
    @GetMapping("/posts")
    public Result<Page<CommunityPost>> getAllPosts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CommunityPost> page = communityService.getPosts(category, pageNum, pageSize);
        return Result.success(page);
    }
}
