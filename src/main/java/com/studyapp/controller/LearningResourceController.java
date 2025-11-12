package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.entity.LearningResource;
import com.studyapp.service.LearningResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习资源控制器
 * Learning Resource Controller
 */
@Api(tags = "学习资源管理")
@RestController
@RequestMapping("/resource")
public class LearningResourceController {
    
    @Autowired
    private LearningResourceService resourceService;
    
    @ApiOperation("创建学习资源")
    @PostMapping
    public Result<LearningResource> createResource(@RequestBody LearningResource resource) {
        LearningResource created = resourceService.createResource(resource);
        return Result.success(created);
    }
    
    @ApiOperation("更新学习资源")
    @PutMapping("/{id}")
    public Result<LearningResource> updateResource(@PathVariable Long id, @RequestBody LearningResource resource) {
        LearningResource updated = resourceService.updateResource(id, resource);
        return Result.success(updated);
    }
    
    @ApiOperation("删除学习资源")
    @DeleteMapping("/{id}")
    public Result<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.success();
    }
    
    @ApiOperation("获取学习资源详情")
    @GetMapping("/{id}")
    public Result<LearningResource> getResource(@PathVariable Long id) {
        LearningResource resource = resourceService.getResource(id);
        return Result.success(resource);
    }
    
    @ApiOperation("分页查询学习资源")
    @GetMapping("/list")
    public Result<Page<LearningResource>> getResources(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<LearningResource> page = resourceService.getResources(subject, type, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("按章节获取资源")
    @GetMapping("/chapter")
    public Result<List<LearningResource>> getResourcesByChapter(
            @RequestParam String subject,
            @RequestParam String chapter) {
        List<LearningResource> resources = resourceService.getResourcesByChapter(subject, chapter);
        return Result.success(resources);
    }
    
    @ApiOperation("发布资源")
    @PostMapping("/{id}/publish")
    public Result<Void> publishResource(@PathVariable Long id) {
        resourceService.publishResource(id);
        return Result.success();
    }
}
