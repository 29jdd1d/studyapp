package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习计划项实体类
 * Study Plan Item Entity
 */
@Entity
@Table(name = "study_plan_item")
@Data
public class StudyPlanItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long planId;
    
    @Column(nullable = false, length = 50)
    private String subject;
    
    @Column(nullable = false, length = 100)
    private String chapter;
    
    @Column(length = 100)
    private String section;
    
    @Column(nullable = false)
    private LocalDate planDate;
    
    @Column(nullable = false)
    private Integer estimatedHours;
    
    @Column
    private Integer actualHours;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Column
    private LocalDateTime completedTime;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
