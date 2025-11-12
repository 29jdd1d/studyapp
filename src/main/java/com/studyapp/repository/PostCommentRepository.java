package com.studyapp.repository;

import com.studyapp.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层
 * Comment Repository
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    List<PostComment> findByPostIdOrderByCreateTimeDesc(Long postId);
    
    List<PostComment> findByParentIdOrderByCreateTimeAsc(Long parentId);
    
    Long countByPostId(Long postId);
}
