package com.studyapp.service;

import com.studyapp.entity.CommunityPost;
import com.studyapp.entity.PostComment;
import com.studyapp.entity.StudyCheckIn;
import com.studyapp.repository.CommunityPostRepository;
import com.studyapp.repository.PostCommentRepository;
import com.studyapp.repository.StudyCheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 社区服务类
 * Community Service
 */
@Service
public class CommunityService {
    
    @Autowired
    private CommunityPostRepository postRepository;
    
    @Autowired
    private PostCommentRepository commentRepository;
    
    @Autowired
    private StudyCheckInRepository checkInRepository;
    
    /**
     * 创建帖子
     */
    @Transactional
    public CommunityPost createPost(CommunityPost post) {
        return postRepository.save(post);
    }
    
    /**
     * 获取帖子列表
     */
    public Page<CommunityPost> getPosts(String category, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "pinned", "createTime"));
        
        if (category != null && !category.isEmpty()) {
            return postRepository.findByCategoryAndPublished(category, true, pageable);
        } else {
            return postRepository.findByPublished(true, pageable);
        }
    }
    
    /**
     * 获取我的帖子
     */
    public Page<CommunityPost> getMyPosts(Long userId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, 
                Sort.by(Sort.Direction.DESC, "createTime"));
        return postRepository.findByUserId(userId, pageable);
    }
    
    /**
     * 获取置顶帖子
     */
    public List<CommunityPost> getPinnedPosts() {
        return postRepository.findByPinnedAndPublished(true, true);
    }
    
    /**
     * 获取帖子详情
     */
    @Transactional
    public CommunityPost getPostDetail(Long id) {
        CommunityPost post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        
        return post;
    }
    
    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Long id) {
        CommunityPost post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }
    
    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    
    /**
     * 添加评论
     */
    @Transactional
    public PostComment addComment(PostComment comment) {
        PostComment saved = commentRepository.save(comment);
        
        // 更新帖子评论数
        CommunityPost post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        
        return saved;
    }
    
    /**
     * 获取评论列表
     */
    public List<PostComment> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreateTimeDesc(postId);
    }
    
    /**
     * 学习打卡
     */
    @Transactional
    public StudyCheckIn checkIn(StudyCheckIn checkIn) {
        // 检查今天是否已打卡
        checkInRepository.findByUserIdAndCheckInDate(checkIn.getUserId(), checkIn.getCheckInDate())
                .ifPresent(existing -> {
                    throw new RuntimeException("今天已经打卡过了");
                });
        
        return checkInRepository.save(checkIn);
    }
    
    /**
     * 获取打卡记录
     */
    public List<StudyCheckIn> getCheckInRecords(Long userId) {
        return checkInRepository.findByUserIdOrderByCheckInDateDesc(userId);
    }
    
    /**
     * 获取连续打卡天数
     */
    public Integer getContinuousCheckInDays(Long userId) {
        List<StudyCheckIn> records = checkInRepository.findByUserIdOrderByCheckInDateDesc(userId);
        
        if (records.isEmpty()) {
            return 0;
        }
        
        int continuousDays = 0;
        LocalDate currentDate = LocalDate.now();
        
        for (StudyCheckIn record : records) {
            if (record.getCheckInDate().equals(currentDate)) {
                continuousDays++;
                currentDate = currentDate.minusDays(1);
            } else {
                break;
            }
        }
        
        return continuousDays;
    }
}
