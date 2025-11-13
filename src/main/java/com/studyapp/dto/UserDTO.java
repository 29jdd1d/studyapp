package com.studyapp.dto;

import lombok.Data;

/**
 * 用户DTO
 * User Data Transfer Object
 */
@Data
public class UserDTO {
    
    private Long id;
    private String username;
    private String nickName;
    private String avatarUrl;
    private String gender;
    private String phone;
    private String email;
    private String targetUniversity;
    private String targetMajor;
    private String examYear;
    private Integer studyDays;
    private Integer studyHours;
    private Integer completedQuestions;
    private Integer correctQuestions;
    private String role;
}
