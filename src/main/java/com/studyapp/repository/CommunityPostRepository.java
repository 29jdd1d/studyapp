package com.studyapp.repository;

import com.studyapp.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社区帖子数据访问层
 * Community Post Repository
 */
@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    
    Page<CommunityPost> findByPublished(Boolean published, Pageable pageable);
    
    Page<CommunityPost> findByCategoryAndPublished(String category, Boolean published, Pageable pageable);
    
    Page<CommunityPost> findByUserId(Long userId, Pageable pageable);
    
    List<CommunityPost> findByPinnedAndPublished(Boolean pinned, Boolean published);
}
