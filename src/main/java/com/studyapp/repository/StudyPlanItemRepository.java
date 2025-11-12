package com.studyapp.repository;

import com.studyapp.entity.StudyPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 学习计划项数据访问层
 * Study Plan Item Repository
 */
@Repository
public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {
    
    List<StudyPlanItem> findByPlanId(Long planId);
    
    List<StudyPlanItem> findByPlanIdAndPlanDate(Long planId, LocalDate planDate);
    
    List<StudyPlanItem> findByPlanIdAndCompleted(Long planId, Boolean completed);
    
    Long countByPlanIdAndCompleted(Long planId, Boolean completed);
}
