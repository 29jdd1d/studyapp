<template>
  <div class="resources-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学习资源列表</span>
          <el-button type="primary" @click="handleAdd">新增资源</el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="资源名称">
          <el-input v-model="searchForm.title" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="视频" value="VIDEO" />
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="题库" value="QUESTION_BANK" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="resourceList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="资源名称" width="200" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'VIDEO'" type="success">视频</el-tag>
            <el-tag v-else-if="row.type === 'DOCUMENT'" type="primary">文档</el-tag>
            <el-tag v-else type="warning">题库</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="chapter" label="章节" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" size="small" @click="handlePublish(row)">
              {{ row.status === 'PUBLISHED' ? '下架' : '发布' }}
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
        @size-change="loadResources"
        @current-change="loadResources"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="资源名称">
          <el-input v-model="editForm.title" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="视频" value="VIDEO" />
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="题库" value="QUESTION_BANK" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="editForm.subject" placeholder="请输入科目" />
        </el-form-item>
        <el-form-item label="章节">
          <el-input v-model="editForm.chapter" placeholder="请输入章节" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="4" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="资源文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
            accept="*/*"
          >
            <template #trigger>
              <el-button type="primary">选择文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                {{ getUploadTip() }}
              </div>
            </template>
          </el-upload>
          <div v-if="uploadProgress > 0 && uploadProgress < 100" style="margin-top: 10px;">
            <el-progress :percentage="uploadProgress" />
          </div>
        </el-form-item>
        <el-form-item label="资源URL" v-if="editForm.fileUrl">
          <el-input v-model="editForm.fileUrl" placeholder="上传文件后自动生成" readonly>
            <template #append>
              <el-button @click="copyUrl" icon="DocumentCopy">复制</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio label="DRAFT">草稿</el-radio>
            <el-radio label="PUBLISHED">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResources, createResource, updateResource, deleteResource } from '../../api/admin'
import { uploadVideo, uploadDocument } from '../../api/upload'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const uploadProgress = ref(0)
const uploadRef = ref(null)
const fileList = ref([])

const searchForm = reactive({
  title: '',
  type: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const resourceList = ref([])
const editForm = ref({})

const dialogTitle = computed(() => isEdit.value ? '编辑资源' : '新增资源')

const getUploadTip = () => {
  const type = editForm.value.type
  if (type === 'VIDEO') {
    return '请选择视频文件，支持mp4、avi、mov等格式'
  } else if (type === 'DOCUMENT') {
    return '请选择文档文件，支持pdf、doc、docx、ppt、pptx等格式'
  }
  return '请先选择资源类型，然后上传对应的文件'
}

const handleFileChange = (file) => {
  fileList.value = [file]
}

const handleFileRemove = () => {
  fileList.value = []
  editForm.value.fileUrl = ''
  uploadProgress.value = 0
}

const copyUrl = () => {
  navigator.clipboard.writeText(editForm.value.fileUrl)
  ElMessage.success('URL已复制到剪贴板')
}

const uploadFile = async () => {
  if (fileList.value.length === 0) {
    return true
  }

  const file = fileList.value[0].raw
  const type = editForm.value.type

  try {
    uploadProgress.value = 10
    let response

    if (type === 'VIDEO') {
      response = await uploadVideo(file)
    } else if (type === 'DOCUMENT') {
      response = await uploadDocument(file)
    } else {
      ElMessage.error('请先选择资源类型')
      uploadProgress.value = 0
      return false
    }

    uploadProgress.value = 100

    if (response.data && response.data.url) {
      editForm.value.fileUrl = response.data.url
      ElMessage.success('文件上传成功')
      return true
    } else {
      ElMessage.error('文件上传失败：未返回URL')
      uploadProgress.value = 0
      return false
    }
  } catch (error) {
    console.error('Upload error:', error)
    ElMessage.error('文件上传失败：' + (error.message || '未知错误'))
    uploadProgress.value = 0
    return false
  }
}

const loadResources = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getResources(params)
    if (response.data) {
      resourceList.value = response.data.list || response.data
      pagination.total = response.data.total || resourceList.value.length
    }
  } catch (error) {
    console.error('Failed to load resources:', error)
    // Mock data for demonstration
    resourceList.value = [
      {
        id: 1,
        title: '高等数学 - 第一章 函数与极限',
        type: 'VIDEO',
        subject: '数学',
        chapter: '第一章',
        description: '函数与极限基础知识讲解',
        fileUrl: 'https://example.com/video1.mp4',
        status: 'PUBLISHED',
        createdAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        title: '英语词汇手册',
        type: 'DOCUMENT',
        subject: '英语',
        chapter: '词汇',
        description: '考研英语核心词汇',
        fileUrl: 'https://example.com/doc1.pdf',
        status: 'PUBLISHED',
        createdAt: '2024-01-02 11:00:00'
      }
    ]
    pagination.total = resourceList.value.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadResources()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.type = ''
  pagination.pageNum = 1
  loadResources()
}

const handleAdd = () => {
  isEdit.value = false
  editForm.value = {
    title: '',
    type: '',
    subject: '',
    chapter: '',
    description: '',
    fileUrl: '',
    status: 'DRAFT'
  }
  fileList.value = []
  uploadProgress.value = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  editForm.value = { ...row }
  fileList.value = []
  uploadProgress.value = 0
  dialogVisible.value = true
}

const handleSave = async () => {
  // Validate form
  if (!editForm.value.title) {
    ElMessage.error('请输入资源名称')
    return
  }
  if (!editForm.value.type) {
    ElMessage.error('请选择资源类型')
    return
  }

  // Upload file first if a new file is selected
  if (fileList.value.length > 0) {
    const uploadSuccess = await uploadFile()
    if (!uploadSuccess) {
      return
    }
  }

  // Check if fileUrl exists
  if (!editForm.value.fileUrl) {
    ElMessage.error('请上传资源文件或输入资源URL')
    return
  }

  try {
    if (isEdit.value) {
      await updateResource(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await createResource(editForm.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fileList.value = []
    uploadProgress.value = 0
    loadResources()
  } catch (error) {
    console.error('Failed to save resource:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handlePublish = async (row) => {
  const newStatus = row.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED'
  try {
    await updateResource(row.id, { ...row, status: newStatus })
    ElMessage.success(newStatus === 'PUBLISHED' ? '发布成功' : '已下架')
    loadResources()
  } catch (error) {
    console.error('Failed to update status:', error)
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该资源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteResource(row.id)
      ElMessage.success('删除成功')
      loadResources()
    } catch (error) {
      console.error('Failed to delete resource:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadResources()
})
</script>

<style scoped>
.resources-container {
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
