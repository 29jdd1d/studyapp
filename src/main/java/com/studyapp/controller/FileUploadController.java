package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.service.CosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * File Upload Controller
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/file")
public class FileUploadController {
    
    @Autowired
    private CosService cosService;
    
    @ApiOperation("上传视频")
    @PostMapping("/upload/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadVideo(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("视频上传成功", data);
        } catch (Exception e) {
            return Result.error("视频上传失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("上传文档")
    @PostMapping("/upload/document")
    public Result<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadDocument(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("文档上传成功", data);
        } catch (Exception e) {
            return Result.error("文档上传失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("上传图片")
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadImage(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("图片上传成功", data);
        } catch (Exception e) {
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
    
    @ApiOperation("上传封面")
    @PostMapping("/upload/cover")
    public Result<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadCover(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success("封面上传成功", data);
        } catch (Exception e) {
            return Result.error("封面上传失败: " + e.getMessage());
        }
    }
}
