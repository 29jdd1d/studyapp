package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * User Entity
 */
@Entity
@Table(name = "sys_user")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String openId;
    
    @Column(length = 50)
    private String nickName;
    
    @Column(length = 255)
    private String avatarUrl;
    
    @Column(length = 10)
    private String gender;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 100)
    private String targetUniversity;
    
    @Column(length = 100)
    private String targetMajor;
    
    @Column(length = 20)
    private String examYear;
    
    @Column(nullable = false)
    private Integer studyDays = 0;
    
    @Column(nullable = false)
    private Integer studyHours = 0;
    
    @Column(nullable = false)
    private Integer completedQuestions = 0;
    
    @Column(nullable = false)
    private Integer correctQuestions = 0;
    
    @Column(length = 20)
    private String role = "USER";
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
