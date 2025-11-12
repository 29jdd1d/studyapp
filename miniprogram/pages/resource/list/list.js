// pages/resource/list/list.js
const resourceApi = require('../../../api/resource.js');

Page({
  data: {
    resources: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad: function () {
    this.loadResources();
  },

  loadResources: function() {
    if (this.data.loading || !this.data.hasMore) return;
    
    this.setData({ loading: true });
    
    resourceApi.getResourceList({
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    }).then(result => {
      const resources = this.data.pageNum === 1 ? result.list : [...this.data.resources, ...result.list];
      this.setData({
        resources: resources,
        loading: false,
        hasMore: result.hasNextPage
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  onReachBottom: function() {
    if (this.data.hasMore) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.loadResources();
    }
  }
});
