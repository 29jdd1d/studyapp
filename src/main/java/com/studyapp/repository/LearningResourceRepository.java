package com.studyapp.repository;

import com.studyapp.entity.LearningResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学习资源数据访问层
 * Learning Resource Repository
 */
@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, Long> {
    
    Page<LearningResource> findBySubjectAndPublished(String subject, Boolean published, Pageable pageable);
    
    Page<LearningResource> findByTypeAndPublished(String type, Boolean published, Pageable pageable);
    
    Page<LearningResource> findBySubjectAndTypeAndPublished(String subject, String type, Boolean published, Pageable pageable);
    
    Page<LearningResource> findByPublished(Boolean published, Pageable pageable);
    
    List<LearningResource> findBySubjectAndChapter(String subject, String chapter);
}
