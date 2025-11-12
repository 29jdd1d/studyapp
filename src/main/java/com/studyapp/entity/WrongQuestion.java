package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 错题本实体类
 * Wrong Question Entity
 */
@Entity
@Table(name = "wrong_question")
@Data
public class WrongQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long questionId;
    
    @Column(nullable = false)
    private Integer wrongCount = 1;
    
    @Column
    private Boolean mastered = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column
    private LocalDateTime lastReviewTime;
}
