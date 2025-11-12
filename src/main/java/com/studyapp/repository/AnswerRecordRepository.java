package com.studyapp.repository;

import com.studyapp.entity.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 答题记录数据访问层
 * Answer Record Repository
 */
@Repository
public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {
    
    List<AnswerRecord> findByUserId(Long userId);
    
    List<AnswerRecord> findByUserIdAndQuestionId(Long userId, Long questionId);
    
    Long countByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
}
