package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学习资源实体类
 * Learning Resource Entity
 */
@Entity
@Table(name = "learning_resource")
@Data
public class LearningResource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 20)
    private String type; // VIDEO, DOCUMENT, QUESTION_BANK
    
    @Column(nullable = false, length = 50)
    private String subject; // 政治、英语、数学、专业课
    
    @Column(length = 100)
    private String chapter;
    
    @Column(length = 100)
    private String section;
    
    @Column(nullable = false, length = 500)
    private String fileUrl;
    
    @Column(length = 500)
    private String coverUrl;
    
    @Column
    private Integer duration; // 视频时长(秒)
    
    @Column
    private Long fileSize; // 文件大小(字节)
    
    @Column(nullable = false)
    private Integer viewCount = 0;
    
    @Column(nullable = false)
    private Integer downloadCount = 0;
    
    @Column(nullable = false)
    private Boolean published = false;
    
    @Column
    private Long uploaderId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
