package com.studyapp.repository;

import com.studyapp.entity.WrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 错题本数据访问层
 * Wrong Question Repository
 */
@Repository
public interface WrongQuestionRepository extends JpaRepository<WrongQuestion, Long> {
    
    List<WrongQuestion> findByUserId(Long userId);
    
    List<WrongQuestion> findByUserIdAndMastered(Long userId, Boolean mastered);
    
    Optional<WrongQuestion> findByUserIdAndQuestionId(Long userId, Long questionId);
}
