package com.studyapp.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * Comment Entity
 */
@Entity
@Table(name = "post_comment")
@Data
public class PostComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column
    private Long parentId; // 父评论ID，用于回复
    
    @Column(nullable = false)
    private Integer likeCount = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
}
