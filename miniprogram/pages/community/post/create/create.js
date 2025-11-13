// pages/community/post/create/create.js
const communityApi = require('../../../../api/community.js');
const uploadApi = require('../../../../api/upload.js');

Page({
  data: {
    categoryOptions: [
      { label: '经验分享', value: 'EXPERIENCE' },
      { label: '资讯', value: 'NEWS' },
      { label: '问答', value: 'QA' },
      { label: '打卡', value: 'CHECKIN' }
    ],
    categoryIndex: -1,
    formData: {
      title: '',
      content: '',
      category: ''
    },
    images: [],
    loading: false,
    loadingText: '处理中...'
  },

  /**
   * 分类选择
   */
  onCategoryChange(e) {
    const index = e.detail.value;
    this.setData({
      categoryIndex: index,
      'formData.category': this.data.categoryOptions[index].value
    });
  },

  /**
   * 标题输入
   */
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    });
  },

  /**
   * 内容输入
   */
  onContentInput(e) {
    this.setData({
      'formData.content': e.detail.value
    });
  },

  /**
   * 选择图片
   */
  chooseImage() {
    const maxCount = 9 - this.data.images.length;
    
    wx.chooseImage({
      count: maxCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFilePaths;
        this.uploadImages(tempFilePaths);
      }
    });
  },

  /**
   * 上传图片
   */
  uploadImages(filePaths) {
    this.setData({ 
      loading: true,
      loadingText: '上传图片中...' 
    });

    const uploadPromises = filePaths.map(filePath => {
      return uploadApi.uploadImage(filePath);
    });

    Promise.all(uploadPromises)
      .then(results => {
        const urls = results.map(res => res.url);
        const newImages = [...this.data.images, ...urls];
        
        this.setData({
          images: newImages,
          loading: false
        });

        wx.showToast({
          title: '上传成功',
          icon: 'success'
        });
      })
      .catch(err => {
        this.setData({ loading: false });
        console.error('上传图片失败:', err);
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
      });
  },

  /**
   * 删除图片
   */
  deleteImage(e) {
    const { index } = e.currentTarget.dataset;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images });
  },

  /**
   * 提交表单
   */
  submit() {
    const { title, content, category } = this.data.formData;
    const { images } = this.data;

    // 验证必填项
    if (!category) {
      wx.showToast({
        title: '请选择分类',
        icon: 'none'
      });
      return;
    }

    if (!title.trim()) {
      wx.showToast({
        title: '请输入标题',
        icon: 'none'
      });
      return;
    }

    if (!content.trim()) {
      wx.showToast({
        title: '请输入内容',
        icon: 'none'
      });
      return;
    }

    this.setData({ 
      loading: true,
      loadingText: '发布中...' 
    });

    const postData = {
      title: title.trim(),
      content: content.trim(),
      category,
      images: images.join(',')
    };

    communityApi.createPost(postData)
      .then(post => {
        this.setData({ loading: false });
        
        wx.showToast({
          title: '发布成功',
          icon: 'success'
        });

        // 跳转到帖子详情页
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/community/post/detail/detail?id=${post.id}`
          });
        }, 1000);
      })
      .catch(err => {
        this.setData({ loading: false });
        console.error('发布帖子失败:', err);
        wx.showToast({
          title: '发布失败，请重试',
          icon: 'none'
        });
      });
  },

  /**
   * 取消
   */
  cancel() {
    if (this.data.formData.title || this.data.formData.content || this.data.images.length > 0) {
      wx.showModal({
        title: '提示',
        content: '确定要放弃编辑吗？',
        success: (res) => {
          if (res.confirm) {
            wx.navigateBack();
          }
        }
      });
    } else {
      wx.navigateBack();
    }
  }
});
