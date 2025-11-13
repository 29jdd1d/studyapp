// pages/community/post/list/list.js
const communityApi = require('../../../../api/community.js');

Page({
  data: {
    categories: [
      { label: '全部', value: '' },
      { label: '经验分享', value: 'EXPERIENCE' },
      { label: '资讯', value: 'NEWS' },
      { label: '问答', value: 'QA' },
      { label: '打卡', value: 'CHECKIN' }
    ],
    activeCategory: '',
    posts: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad(options) {
    if (options.category) {
      this.setData({ activeCategory: options.category });
    }
    this.loadPosts();
  },

  /**
   * 选择分类
   */
  selectCategory(e) {
    const { value } = e.currentTarget.dataset;
    this.setData({ activeCategory: value });
    this.loadPosts(true);
  },

  /**
   * 加载帖子列表
   */
  loadPosts(reset = false) {
    if (this.data.loading) return;

    if (reset) {
      this.setData({
        pageNum: 1,
        posts: [],
        hasMore: true
      });
    }

    this.setData({ loading: true });

    const { activeCategory, pageNum, pageSize } = this.data;
    const params = {
      category: activeCategory || undefined,
      pageNum,
      pageSize
    };

    communityApi.getPostList(params)
      .then(result => {
        const newPosts = reset ? result.list : [...this.data.posts, ...result.list];
        
        // 格式化帖子数据
        const formattedPosts = newPosts.map(post => {
          post.createTime = this.formatTime(post.createTime);
          return post;
        });

        this.setData({
          posts: formattedPosts,
          loading: false,
          hasMore: result.list.length >= pageSize
        });
      })
      .catch(err => {
        console.error('加载帖子列表失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败',
          icon: 'none'
        });
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

    // 1分钟内
    if (diff < 60000) {
      return '刚刚';
    }
    
    // 1小时内
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前';
    }
    
    // 今天
    if (now.toDateString() === date.toDateString()) {
      return Math.floor(diff / 3600000) + '小时前';
    }
    
    // 昨天
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);
    if (yesterday.toDateString() === date.toDateString()) {
      return '昨天';
    }
    
    // 更早
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${month}-${day}`;
  },

  /**
   * 加载更多
   */
  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      pageNum: this.data.pageNum + 1
    });
    this.loadPosts();
  },

  /**
   * 点赞帖子
   */
  likePost(e) {
    const { id, index } = e.currentTarget.dataset;

    communityApi.likePost(id)
      .then(() => {
        // 更新本地状态
        const posts = this.data.posts;
        posts[index].liked = !posts[index].liked;
        posts[index].likes = posts[index].likes + (posts[index].liked ? 1 : -1);
        
        this.setData({ posts });
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
   * 跳转到帖子详情
   */
  goToPostDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/community/post/detail/detail?id=${id}`
    });
  },

  /**
   * 创建帖子
   */
  createPost() {
    wx.navigateTo({
      url: '/pages/community/post/create/create'
    });
  },

  /**
   * 下拉刷新
   */
  onPullDownRefresh() {
    this.loadPosts(true);
    wx.stopPullDownRefresh();
  },

  /**
   * 上拉加载
   */
  onReachBottom() {
    this.loadMore();
  }
});
