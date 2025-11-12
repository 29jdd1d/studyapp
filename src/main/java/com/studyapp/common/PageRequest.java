package com.studyapp.common;

import lombok.Data;

/**
 * 分页查询请求参数
 * Page Request
 */
@Data
public class PageRequest {
    
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sortBy;
    private String sortOrder = "desc";
}
