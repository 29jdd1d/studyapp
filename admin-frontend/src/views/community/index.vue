<template>
  <div class="community-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>社区帖子管理</span>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="帖子标题">
          <el-input v-model="searchForm.title" placeholder="请输入帖子标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="postList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" show-overflow-tooltip width="300" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'EXPERIENCE'">经验分享</el-tag>
            <el-tag v-else-if="row.type === 'QUESTION'" type="warning">问题求助</el-tag>
            <el-tag v-else-if="row.type === 'NEWS'" type="success">资讯</el-tag>
            <el-tag v-else type="info">打卡</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
            <el-tag v-else-if="row.status === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PENDING'" 
              link 
              type="success" 
              size="small" 
              @click="handleAudit(row, 'PUBLISHED')"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              link 
              type="warning" 
              size="small" 
              @click="handleAudit(row, 'REJECTED')"
            >
              拒绝
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadPosts"
        @current-change="loadPosts"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPosts, auditPost, deletePost } from '../../api/admin'

const loading = ref(false)

const searchForm = reactive({
  title: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const postList = ref([])

const loadPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getPosts(params)
    if (response.data) {
      postList.value = response.data.list || response.data
      pagination.total = response.data.total || postList.value.length
    }
  } catch (error) {
    console.error('Failed to load posts:', error)
    // Mock data
    postList.value = [
      {
        id: 1,
        title: '2024考研经验分享',
        content: '经过一年的努力，成功上岸...',
        author: '张三',
        type: 'EXPERIENCE',
        status: 'PUBLISHED',
        likeCount: 128,
        commentCount: 45,
        createdAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        title: '高数第一章重点整理',
        content: '函数与极限是考研数学的基础...',
        author: '李四',
        type: 'EXPERIENCE',
        status: 'PENDING',
        likeCount: 56,
        commentCount: 12,
        createdAt: '2024-01-02 11:00:00'
      },
      {
        id: 3,
        title: '今日学习打卡',
        content: '今天完成了第一章的学习...',
        author: '王五',
        type: 'CHECKIN',
        status: 'PUBLISHED',
        likeCount: 23,
        commentCount: 5,
        createdAt: '2024-01-03 12:00:00'
      }
    ]
    pagination.total = postList.value.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadPosts()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = ''
  pagination.pageNum = 1
  loadPosts()
}

const handleAudit = async (row, status) => {
  const action = status === 'PUBLISHED' ? '通过' : '拒绝'
  ElMessageBox.confirm(`确定要${action}该帖子吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await auditPost(row.id, status)
      ElMessage.success(`${action}成功`)
      loadPosts()
    } catch (error) {
      console.error('Failed to audit post:', error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePost(row.id)
      ElMessage.success('删除成功')
      loadPosts()
    } catch (error) {
      console.error('Failed to delete post:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.community-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
