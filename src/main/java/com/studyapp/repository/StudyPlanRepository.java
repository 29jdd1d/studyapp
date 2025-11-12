package com.studyapp.repository;

import com.studyapp.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学习计划数据访问层
 * Study Plan Repository
 */
@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    
    List<StudyPlan> findByUserId(Long userId);
    
    List<StudyPlan> findByUserIdAndStatus(Long userId, String status);
}
