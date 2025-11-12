package com.studyapp.service;

import com.studyapp.entity.LearningResource;
import com.studyapp.repository.LearningResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 学习资源服务类
 * Learning Resource Service
 */
@Service
public class LearningResourceService {
    
    @Autowired
    private LearningResourceRepository resourceRepository;
    
    /**
     * 创建学习资源
     */
    @Transactional
    public LearningResource createResource(LearningResource resource) {
        return resourceRepository.save(resource);
    }
    
    /**
     * 更新学习资源
     */
    @Transactional
    public LearningResource updateResource(Long id, LearningResource resource) {
        LearningResource existing = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资源不存在"));
        
        if (resource.getTitle() != null) {
            existing.setTitle(resource.getTitle());
        }
        if (resource.getDescription() != null) {
            existing.setDescription(resource.getDescription());
        }
        if (resource.getSubject() != null) {
            existing.setSubject(resource.getSubject());
        }
        if (resource.getChapter() != null) {
            existing.setChapter(resource.getChapter());
        }
        if (resource.getSection() != null) {
            existing.setSection(resource.getSection());
        }
        if (resource.getFileUrl() != null) {
            existing.setFileUrl(resource.getFileUrl());
        }
        if (resource.getCoverUrl() != null) {
            existing.setCoverUrl(resource.getCoverUrl());
        }
        if (resource.getPublished() != null) {
            existing.setPublished(resource.getPublished());
        }
        
        return resourceRepository.save(existing);
    }
    
    /**
     * 删除学习资源
     */
    @Transactional
    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
    
    /**
     * 获取学习资源详情
     */
    public LearningResource getResource(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资源不存在"));
        
        // 增加浏览次数
        resource.setViewCount(resource.getViewCount() + 1);
        resourceRepository.save(resource);
        
        return resource;
    }
    
    /**
     * 分页查询学习资源
     */
    public Page<LearningResource> getResources(String subject, String type, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        if (subject != null && type != null) {
            return resourceRepository.findBySubjectAndTypeAndPublished(subject, type, true, pageable);
        } else if (subject != null) {
            return resourceRepository.findBySubjectAndPublished(subject, true, pageable);
        } else if (type != null) {
            return resourceRepository.findByTypeAndPublished(type, true, pageable);
        } else {
            return resourceRepository.findByPublished(true, pageable);
        }
    }
    
    /**
     * 按章节获取资源
     */
    public List<LearningResource> getResourcesByChapter(String subject, String chapter) {
        return resourceRepository.findBySubjectAndChapter(subject, chapter);
    }
    
    /**
     * 发布资源
     */
    @Transactional
    public void publishResource(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("资源不存在"));
        resource.setPublished(true);
        resourceRepository.save(resource);
    }
}
