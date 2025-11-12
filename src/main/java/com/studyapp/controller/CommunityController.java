package com.studyapp.controller;

import com.studyapp.common.Result;
import com.studyapp.entity.CommunityPost;
import com.studyapp.entity.PostComment;
import com.studyapp.entity.StudyCheckIn;
import com.studyapp.service.CommunityService;
import com.studyapp.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 社区控制器
 * Community Controller
 */
@Api(tags = "社区交流")
@RestController
@RequestMapping("/community")
public class CommunityController {
    
    @Autowired
    private CommunityService communityService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("创建帖子")
    @PostMapping("/post")
    public Result<CommunityPost> createPost(HttpServletRequest request, @RequestBody CommunityPost post) {
        Long userId = getUserIdFromRequest(request);
        post.setUserId(userId);
        CommunityPost created = communityService.createPost(post);
        return Result.success(created);
    }
    
    @ApiOperation("获取帖子列表")
    @GetMapping("/post/list")
    public Result<Page<CommunityPost>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CommunityPost> page = communityService.getPosts(category, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取我的帖子")
    @GetMapping("/post/my")
    public Result<Page<CommunityPost>> getMyPosts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getUserIdFromRequest(request);
        Page<CommunityPost> page = communityService.getMyPosts(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取置顶帖子")
    @GetMapping("/post/pinned")
    public Result<List<CommunityPost>> getPinnedPosts() {
        List<CommunityPost> posts = communityService.getPinnedPosts();
        return Result.success(posts);
    }
    
    @ApiOperation("获取帖子详情")
    @GetMapping("/post/{id}")
    public Result<CommunityPost> getPostDetail(@PathVariable Long id) {
        CommunityPost post = communityService.getPostDetail(id);
        return Result.success(post);
    }
    
    @ApiOperation("点赞帖子")
    @PostMapping("/post/{id}/like")
    public Result<Void> likePost(@PathVariable Long id) {
        communityService.likePost(id);
        return Result.success();
    }
    
    @ApiOperation("删除帖子")
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return Result.success();
    }
    
    @ApiOperation("添加评论")
    @PostMapping("/comment")
    public Result<PostComment> addComment(HttpServletRequest request, @RequestBody PostComment comment) {
        Long userId = getUserIdFromRequest(request);
        comment.setUserId(userId);
        PostComment created = communityService.addComment(comment);
        return Result.success(created);
    }
    
    @ApiOperation("获取评论列表")
    @GetMapping("/comment/{postId}")
    public Result<List<PostComment>> getComments(@PathVariable Long postId) {
        List<PostComment> comments = communityService.getComments(postId);
        return Result.success(comments);
    }
    
    @ApiOperation("学习打卡")
    @PostMapping("/checkin")
    public Result<StudyCheckIn> checkIn(HttpServletRequest request, @RequestBody StudyCheckIn checkIn) {
        Long userId = getUserIdFromRequest(request);
        checkIn.setUserId(userId);
        StudyCheckIn created = communityService.checkIn(checkIn);
        return Result.success(created);
    }
    
    @ApiOperation("获取打卡记录")
    @GetMapping("/checkin/records")
    public Result<List<StudyCheckIn>> getCheckInRecords(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<StudyCheckIn> records = communityService.getCheckInRecords(userId);
        return Result.success(records);
    }
    
    @ApiOperation("获取连续打卡天数")
    @GetMapping("/checkin/continuous")
    public Result<Integer> getContinuousCheckInDays(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Integer days = communityService.getContinuousCheckInDays(userId);
        return Result.success(days);
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
