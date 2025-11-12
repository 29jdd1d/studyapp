package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 题目实体类
 * Question Entity
 */
@Entity
@Table(name = "question")
@Data
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String subject;
    
    @Column(length = 100)
    private String chapter;
    
    @Column(length = 100)
    private String knowledgePoint;
    
    @Column(nullable = false, length = 20)
    private String type; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, ESSAY
    
    @Column(nullable = false, length = 20)
    private String difficulty; // EASY, MEDIUM, HARD
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String optionA;
    
    @Column(columnDefinition = "TEXT")
    private String optionB;
    
    @Column(columnDefinition = "TEXT")
    private String optionC;
    
    @Column(columnDefinition = "TEXT")
    private String optionD;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
    
    @Column(columnDefinition = "TEXT")
    private String analysis;
    
    @Column(length = 20)
    private String year;
    
    @Column(nullable = false)
    private Integer answerCount = 0;
    
    @Column(nullable = false)
    private Integer correctCount = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
