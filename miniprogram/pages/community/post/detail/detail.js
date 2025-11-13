// pages/community/post/detail/detail.js
const communityApi = require('../../../../api/community.js');

Page({
  data: {
    post: null,
    comments: [],
    commentText: '',
    loading: true,
    error: null
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.postId = id;
      this.loadPostDetail();
      this.loadComments();
    } else {
      this.setData({
        loading: false,
        error: '帖子ID不存在'
      });
    }
  },

  /**
   * 加载帖子详情
   */
  loadPostDetail() {
    this.setData({ loading: true, error: null });

    communityApi.getPostDetail(this.postId)
      .then(post => {
        post.createTime = this.formatTime(post.createTime);
        this.setData({
          post,
          loading: false
        });
      })
      .catch(err => {
        console.error('加载帖子详情失败:', err);
        this.setData({
          loading: false,
          error: '加载失败，请重试'
        });
      });
  },

  /**
   * 加载评论列表
   */
  loadComments() {
    communityApi.getComments(this.postId)
      .then(comments => {
        comments = comments.map(comment => {
          comment.createTime = this.formatTime(comment.createTime);
          return comment;
        });
        
        this.setData({ comments });
      })
      .catch(err => {
        console.error('加载评论失败:', err);
      });
  },

  /**
   * 格式化时间
   */
  formatTime(dateStr) {
    if (!dateStr) return '';
    
    const date = new Date(dateStr);
    const now = new Date();
    const diff = now - date;

    if (diff < 60000) {
      return '刚刚';
    }
    
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前';
    }
    
    if (now.toDateString() === date.toDateString()) {
      return Math.floor(diff / 3600000) + '小时前';
    }
    
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);
    if (yesterday.toDateString() === date.toDateString()) {
      return '昨天 ' + String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0');
    }
    
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${month}-${day} ${hours}:${minutes}`;
  },

  /**
   * 点赞帖子
   */
  likePost() {
    const { post } = this.data;
    
    communityApi.likePost(this.postId)
      .then(() => {
        post.liked = !post.liked;
        post.likes = post.likes + (post.liked ? 1 : -1);
        this.setData({ post });
      })
      .catch(err => {
        console.error('点赞失败:', err);
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        });
      });
  },

  /**
   * 预览图片
   */
  previewImage(e) {
    const { urls, current } = e.currentTarget.dataset;
    const urlList = urls.split(',');
    
    wx.previewImage({
      urls: urlList,
      current
    });
  },

  /**
   * 评论输入
   */
  onCommentInput(e) {
    this.setData({
      commentText: e.detail.value
    });
  },

  /**
   * 提交评论
   */
  submitComment() {
    const { commentText } = this.data;

    if (!commentText.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '发送中...' });

    const commentData = {
      postId: this.postId,
      content: commentText.trim()
    };

    communityApi.addComment(commentData)
      .then(() => {
        wx.hideLoading();
        wx.showToast({
          title: '评论成功',
          icon: 'success'
        });

        this.setData({ commentText: '' });
        
        // 重新加载评论列表
        this.loadComments();
        
        // 更新评论数
        const post = this.data.post;
        post.comments = (post.comments || 0) + 1;
        this.setData({ post });
      })
      .catch(err => {
        wx.hideLoading();
        console.error('评论失败:', err);
        wx.showToast({
          title: '评论失败',
          icon: 'none'
        });
      });
  },

  /**
   * 重试
   */
  retry() {
    this.loadPostDetail();
    this.loadComments();
  },

  /**
   * 分享
   */
  onShareAppMessage() {
    const { post } = this.data;
    return {
      title: post ? post.title : '考研学习社区',
      path: `/pages/community/post/detail/detail?id=${this.postId}`
    };
  }
});
