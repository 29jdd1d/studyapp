package com.studyapp.repository;

import com.studyapp.entity.StudyCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 学习打卡数据访问层
 * Study Check-in Repository
 */
@Repository
public interface StudyCheckInRepository extends JpaRepository<StudyCheckIn, Long> {
    
    List<StudyCheckIn> findByUserIdOrderByCheckInDateDesc(Long userId);
    
    Optional<StudyCheckIn> findByUserIdAndCheckInDate(Long userId, LocalDate checkInDate);
    
    Long countByUserId(Long userId);
}
