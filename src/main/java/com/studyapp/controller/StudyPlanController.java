package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.entity.StudyPlan;
import com.studyapp.entity.StudyPlanItem;
import com.studyapp.service.StudyPlanService;
import com.studyapp.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * 学习计划控制器
 * Study Plan Controller
 */
@Api(tags = "个性化学习计划")
@RestController
@RequestMapping("/plan")
public class StudyPlanController {
    
    @Autowired
    private StudyPlanService planService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("创建学习计划")
    @PostMapping
    public Result<StudyPlan> createPlan(@RequestBody StudyPlan plan, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        plan.setUserId(userId);
        StudyPlan created = planService.createPlan(plan);
        return Result.success(created);
    }
    
    @ApiOperation("获取我的学习计划")
    @GetMapping("/my")
    public Result<List<StudyPlan>> getMyPlans(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<StudyPlan> plans = planService.getUserPlans(userId);
        return Result.success(plans);
    }
    
    @ApiOperation("获取活跃的学习计划")
    @GetMapping("/active")
    public Result<List<StudyPlan>> getActivePlans(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<StudyPlan> plans = planService.getActivePlans(userId);
        return Result.success(plans);
    }
    
    @ApiOperation("获取计划详情")
    @GetMapping("/{id}")
    public Result<StudyPlan> getPlanDetail(@PathVariable Long id) {
        StudyPlan plan = planService.getPlanDetail(id);
        return Result.success(plan);
    }
    
    @ApiOperation("更新计划状态")
    @PutMapping("/{id}/status")
    public Result<Void> updatePlanStatus(@PathVariable Long id, @RequestParam String status) {
        planService.updatePlanStatus(id, status);
        return Result.success();
    }
    
    @ApiOperation("添加计划项")
    @PostMapping("/item")
    public Result<StudyPlanItem> addPlanItem(@RequestBody StudyPlanItem item) {
        StudyPlanItem created = planService.addPlanItem(item);
        return Result.success(created);
    }
    
    @ApiOperation("获取计划项列表")
    @GetMapping("/{planId}/items")
    public Result<List<StudyPlanItem>> getPlanItems(@PathVariable Long planId) {
        List<StudyPlanItem> items = planService.getPlanItems(planId);
        return Result.success(items);
    }
    
    @ApiOperation("获取今日计划")
    @GetMapping("/{planId}/today")
    public Result<List<StudyPlanItem>> getTodayPlanItems(@PathVariable Long planId) {
        List<StudyPlanItem> items = planService.getTodayPlanItems(planId);
        return Result.success(items);
    }
    
    @ApiOperation("完成计划项")
    @PostMapping("/item/{itemId}/complete")
    public Result<Void> completePlanItem(@PathVariable Long itemId, @RequestParam Integer actualHours) {
        planService.completePlanItem(itemId, actualHours);
        return Result.success();
    }
    
    @ApiOperation("生成推荐学习计划")
    @PostMapping("/recommend")
    public Result<StudyPlan> generateRecommendedPlan(
            HttpServletRequest request,
            @RequestParam String targetUniversity,
            @RequestParam String targetMajor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate) {
        Long userId = getUserIdFromRequest(request);
        StudyPlan plan = planService.generateRecommendedPlan(userId, targetUniversity, targetMajor, examDate);
        return Result.success(plan);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
