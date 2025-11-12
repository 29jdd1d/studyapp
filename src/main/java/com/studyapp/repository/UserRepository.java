package com.studyapp.repository;

import com.studyapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByOpenId(String openId);
    
    boolean existsByOpenId(String openId);
}
