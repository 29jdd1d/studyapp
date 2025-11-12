package com.studyapp.service;

import com.studyapp.entity.StudyPlan;
import com.studyapp.entity.StudyPlanItem;
import com.studyapp.repository.StudyPlanItemRepository;
import com.studyapp.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 学习计划服务类
 * Study Plan Service
 */
@Service
public class StudyPlanService {
    
    @Autowired
    private StudyPlanRepository planRepository;
    
    @Autowired
    private StudyPlanItemRepository planItemRepository;
    
    /**
     * 创建学习计划
     */
    @Transactional
    public StudyPlan createPlan(StudyPlan plan) {
        // 计算总天数
        long days = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        plan.setTotalDays((int) days);
        return planRepository.save(plan);
    }
    
    /**
     * 获取用户的学习计划
     */
    public List<StudyPlan> getUserPlans(Long userId) {
        return planRepository.findByUserId(userId);
    }
    
    /**
     * 获取活跃的学习计划
     */
    public List<StudyPlan> getActivePlans(Long userId) {
        return planRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }
    
    /**
     * 获取计划详情
     */
    public StudyPlan getPlanDetail(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("学习计划不存在"));
    }
    
    /**
     * 更新计划状态
     */
    @Transactional
    public void updatePlanStatus(Long planId, String status) {
        StudyPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("学习计划不存在"));
        plan.setStatus(status);
        planRepository.save(plan);
    }
    
    /**
     * 添加计划项
     */
    @Transactional
    public StudyPlanItem addPlanItem(StudyPlanItem item) {
        return planItemRepository.save(item);
    }
    
    /**
     * 获取计划的所有项
     */
    public List<StudyPlanItem> getPlanItems(Long planId) {
        return planItemRepository.findByPlanId(planId);
    }
    
    /**
     * 获取今日计划
     */
    public List<StudyPlanItem> getTodayPlanItems(Long planId) {
        return planItemRepository.findByPlanIdAndPlanDate(planId, LocalDate.now());
    }
    
    /**
     * 完成计划项
     */
    @Transactional
    public void completePlanItem(Long itemId, Integer actualHours) {
        StudyPlanItem item = planItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("计划项不存在"));
        
        item.setCompleted(true);
        item.setCompletedTime(LocalDateTime.now());
        item.setActualHours(actualHours);
        planItemRepository.save(item);
        
        // 更新计划进度
        updatePlanProgress(item.getPlanId());
    }
    
    /**
     * 更新计划进度
     */
    @Transactional
    public void updatePlanProgress(Long planId) {
        StudyPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("学习计划不存在"));
        
        Long totalItems = planItemRepository.count();
        Long completedItems = planItemRepository.countByPlanIdAndCompleted(planId, true);
        
        if (totalItems > 0) {
            int progress = (int) ((completedItems * 100) / totalItems);
            plan.setProgress(progress);
            plan.setCompletedDays(completedItems.intValue());
            planRepository.save(plan);
        }
    }
    
    /**
     * 生成推荐学习计划
     */
    @Transactional
    public StudyPlan generateRecommendedPlan(Long userId, String targetUniversity, String targetMajor, LocalDate examDate) {
        // 创建计划
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setTitle("考研备考计划 - " + targetUniversity);
        plan.setDescription("针对" + targetMajor + "专业的个性化学习计划");
        plan.setTargetUniversity(targetUniversity);
        plan.setTargetMajor(targetMajor);
        plan.setStartDate(LocalDate.now());
        plan.setEndDate(examDate);
        
        long days = ChronoUnit.DAYS.between(LocalDate.now(), examDate) + 1;
        plan.setTotalDays((int) days);
        
        StudyPlan savedPlan = planRepository.save(plan);
        
        // 生成学习计划项（简化版）
        generatePlanItems(savedPlan.getId(), days);
        
        return savedPlan;
    }
    
    /**
     * 生成计划项
     */
    private void generatePlanItems(Long planId, long totalDays) {
        String[] subjects = {"政治", "英语", "数学", "专业课"};
        LocalDate currentDate = LocalDate.now();
        
        // 简化的计划生成逻辑
        for (int i = 0; i < totalDays && i < 30; i++) { // 仅生成前30天的计划
            for (String subject : subjects) {
                StudyPlanItem item = new StudyPlanItem();
                item.setPlanId(planId);
                item.setSubject(subject);
                item.setChapter("第" + ((i % 10) + 1) + "章");
                item.setPlanDate(currentDate.plusDays(i));
                item.setEstimatedHours(2);
                item.setContent(subject + "学习内容");
                planItemRepository.save(item);
            }
        }
    }
}
