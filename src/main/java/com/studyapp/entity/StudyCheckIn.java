package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习打卡实体类
 * Study Check-in Entity
 */
@Entity
@Table(name = "study_checkin")
@Data
public class StudyCheckIn {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private LocalDate checkInDate;
    
    @Column(nullable = false)
    private Integer studyHours;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    @Column(length = 1000)
    private String images;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
}
