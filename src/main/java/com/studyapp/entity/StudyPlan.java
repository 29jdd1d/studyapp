package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习计划实体类
 * Study Plan Entity
 */
@Entity
@Table(name = "study_plan")
@Data
public class StudyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 100)
    private String targetUniversity;
    
    @Column(nullable = false, length = 100)
    private String targetMajor;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Integer totalDays;
    
    @Column(nullable = false)
    private Integer completedDays = 0;
    
    @Column(nullable = false)
    private Integer progress = 0; // 进度百分比
    
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, PAUSED
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
