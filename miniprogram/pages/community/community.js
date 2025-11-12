// pages/community/community.js - 社区页面
const communityApi = require('../../api/community.js');

Page({
  data: {
    activeTab: 0,
    tabs: ['全部', '资讯', '经验分享', '讨论'],
    categories: ['', 'NEWS', 'EXPERIENCE', 'DISCUSSION'],
    posts: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad: function () {
    this.loadPosts();
  },

  onShow: function() {
    // 刷新列表
    this.setData({ pageNum: 1, hasMore: true });
    this.loadPosts();
  },

  /**
   * 切换标签
   */
  switchTab: function(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      activeTab: index,
      pageNum: 1,
      hasMore: true,
      posts: []
    });
    this.loadPosts();
  },

  /**
   * 加载帖子列表
   */
  loadPosts: function() {
    if (this.data.loading || !this.data.hasMore) return;

    this.setData({ loading: true });

    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };

    const category = this.data.categories[this.data.activeTab];
    if (category) {
      params.category = category;
    }

    communityApi.getPostList(params).then(result => {
      const posts = this.data.pageNum === 1 ? result.list : [...this.data.posts, ...result.list];
      this.setData({
        posts: posts,
        loading: false,
        hasMore: result.hasNextPage
      });
    }).catch(err => {
      this.setData({ loading: false });
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh: function() {
    this.setData({ pageNum: 1, hasMore: true });
    this.loadPosts();
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉加载更多
   */
  onReachBottom: function() {
    if (this.data.hasMore) {
      this.setData({
        pageNum: this.data.pageNum + 1
      });
      this.loadPosts();
    }
  },

  /**
   * 跳转到帖子详情
   */
  goToPostDetail: function(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/community/post/detail/detail?id=${id}`
    });
  },

  /**
   * 发布帖子
   */
  createPost: function() {
    wx.navigateTo({
      url: '/pages/community/post/create/create'
    });
  },

  /**
   * 点赞帖子
   */
  likePost: function(e) {
    const id = e.currentTarget.dataset.id;
    const index = e.currentTarget.dataset.index;

    communityApi.likePost(id).then(() => {
      const posts = this.data.posts;
      posts[index].likes = (posts[index].likes || 0) + 1;
      this.setData({ posts });
      
      wx.showToast({
        title: '点赞成功',
        icon: 'success'
      });
    }).catch(err => {
      wx.showToast({
        title: '点赞失败',
        icon: 'none'
      });
    });
  }
});
