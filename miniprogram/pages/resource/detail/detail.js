// pages/resource/detail/detail.js
const resourceApi = require('../../../api/resource.js');

Page({
  data: {
    resource: null,
    relatedResources: [],
    note: '',
    loading: true,
    error: null
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.resourceId = id;
      this.loadResourceDetail();
    } else {
      this.setData({
        loading: false,
        error: '资源ID不存在'
      });
    }
  },

  /**
   * 加载资源详情
   */
  loadResourceDetail() {
    this.setData({ loading: true, error: null });

    resourceApi.getResourceDetail(this.resourceId)
      .then(resource => {
        this.setData({
          resource,
          loading: false
        });
        // 加载相关资源
        this.loadRelatedResources(resource.subject, resource.chapter);
      })
      .catch(err => {
        console.error('加载资源详情失败:', err);
        this.setData({
          loading: false,
          error: '加载失败，请重试'
        });
      });
  },

  /**
   * 加载相关资源
   */
  loadRelatedResources(subject, chapter) {
    const params = {
      subject,
      chapter,
      pageNum: 1,
      pageSize: 5
    };

    resourceApi.getResourceList(params)
      .then(result => {
        // 过滤掉当前资源
        const relatedResources = (result.list || [])
          .filter(item => item.id !== this.resourceId)
          .slice(0, 3);
        
        this.setData({ relatedResources });
      })
      .catch(err => {
        console.error('加载相关资源失败:', err);
      });
  },

  /**
   * 预览文档
   */
  previewDocument() {
    const { resource } = this.data;
    if (!resource || !resource.contentUrl) {
      wx.showToast({
        title: '文档地址无效',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '加载中...' });
    
    // 下载文件
    wx.downloadFile({
      url: resource.contentUrl,
      success: (res) => {
        wx.hideLoading();
        const filePath = res.tempFilePath;
        
        // 打开文档
        wx.openDocument({
          filePath,
          fileType: this.getFileType(resource.fileName),
          success: () => {
            console.log('打开文档成功');
          },
          fail: (err) => {
            console.error('打开文档失败:', err);
            wx.showToast({
              title: '无法打开文档',
              icon: 'none'
            });
          }
        });
      },
      fail: (err) => {
        wx.hideLoading();
        console.error('下载文档失败:', err);
        wx.showToast({
          title: '下载失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 获取文件类型
   */
  getFileType(fileName) {
    if (!fileName) return 'pdf';
    const ext = fileName.split('.').pop().toLowerCase();
    return ext || 'pdf';
  },

  /**
   * 开始练习
   */
  goToPractice() {
    wx.navigateTo({
      url: `/pages/question/practice/practice?resourceId=${this.resourceId}`
    });
  },

  /**
   * 笔记输入
   */
  onNoteInput(e) {
    this.setData({
      note: e.detail.value
    });
  },

  /**
   * 保存笔记
   */
  saveNote() {
    const { note } = this.data;
    
    if (!note.trim()) {
      wx.showToast({
        title: '请输入笔记内容',
        icon: 'none'
      });
      return;
    }

    // 保存到本地存储
    const key = `note_${this.resourceId}`;
    wx.setStorage({
      key,
      data: note,
      success: () => {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        });
      },
      fail: (err) => {
        console.error('保存笔记失败:', err);
        wx.showToast({
          title: '保存失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 跳转到相关资源
   */
  goToResource(e) {
    const { id } = e.currentTarget.dataset;
    wx.redirectTo({
      url: `/pages/resource/detail/detail?id=${id}`
    });
  },

  /**
   * 重试加载
   */
  retry() {
    this.loadResourceDetail();
  },

  /**
   * 页面显示时加载笔记
   */
  onShow() {
    if (this.resourceId) {
      const key = `note_${this.resourceId}`;
      wx.getStorage({
        key,
        success: (res) => {
          this.setData({ note: res.data });
        },
        fail: () => {
          // 没有保存的笔记
        }
      });
    }
  }
});
