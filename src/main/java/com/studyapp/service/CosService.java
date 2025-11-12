package com.studyapp.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS文件上传服务
 * Tencent Cloud COS File Upload Service
 */
@Service
public class CosService {
    
    @Autowired
    private COSClient cosClient;
    
    @Value("${tencent.cos.bucket-name}")
    private String bucketName;
    
    @Value("${tencent.cos.base-url}")
    private String baseUrl;
    
    /**
     * 上传文件到COS
     * @param file 文件
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        
        // 生成唯一文件名
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;
        
        InputStream inputStream = file.getInputStream();
        
        // 设置对象元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        
        // 上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        
        // 返回文件URL
        return baseUrl + "/" + fileName;
    }
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith(baseUrl)) {
            String key = fileUrl.substring(baseUrl.length() + 1);
            cosClient.deleteObject(bucketName, key);
        }
    }
    
    /**
     * 上传视频文件
     */
    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, "videos");
    }
    
    /**
     * 上传文档文件
     */
    public String uploadDocument(MultipartFile file) throws IOException {
        return uploadFile(file, "documents");
    }
    
    /**
     * 上传图片文件
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "images");
    }
    
    /**
     * 上传封面图片
     */
    public String uploadCover(MultipartFile file) throws IOException {
        return uploadFile(file, "covers");
    }
}
