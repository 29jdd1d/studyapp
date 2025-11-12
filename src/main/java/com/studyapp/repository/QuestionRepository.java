package com.studyapp.repository;

import com.studyapp.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目数据访问层
 * Question Repository
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Page<Question> findBySubject(String subject, Pageable pageable);
    
    Page<Question> findBySubjectAndChapter(String subject, String chapter, Pageable pageable);
    
    Page<Question> findBySubjectAndType(String subject, String type, Pageable pageable);
    
    Page<Question> findByYear(String year, Pageable pageable);
    
    List<Question> findBySubjectAndKnowledgePoint(String subject, String knowledgePoint);
}
