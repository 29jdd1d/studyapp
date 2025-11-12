package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 答题记录实体类
 * Answer Record Entity
 */
@Entity
@Table(name = "answer_record")
@Data
public class AnswerRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long questionId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String userAnswer;
    
    @Column(nullable = false)
    private Boolean isCorrect;
    
    @Column
    private Integer timeSpent; // 答题时间(秒)
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime answerTime;
}
