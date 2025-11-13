<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="userList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ row.status === 'ACTIVE' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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
        @size-change="loadUsers"
        @current-change="loadUsers"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- Edit Dialog -->
    <el-dialog v-model="dialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio label="ACTIVE">正常</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUser, deleteUser } from '../../api/admin'

const loading = ref(false)
const dialogVisible = ref(false)

const searchForm = reactive({
  username: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const userList = ref([])
const editForm = ref({})

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getUsers(params)
    if (response.data) {
      userList.value = response.data.list || response.data
      pagination.total = response.data.total || userList.value.length
    }
  } catch (error) {
    console.error('Failed to load users:', error)
    // Mock data for demonstration
    userList.value = [
      {
        id: 1,
        username: 'user001',
        nickname: '张三',
        email: 'zhangsan@example.com',
        phone: '13800138000',
        status: 'ACTIVE',
        createdAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        username: 'user002',
        nickname: '李四',
        email: 'lisi@example.com',
        phone: '13800138001',
        status: 'ACTIVE',
        createdAt: '2024-01-02 11:00:00'
      }
    ]
    pagination.total = userList.value.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.username = ''
  pagination.pageNum = 1
  loadUsers()
}

const handleEdit = (row) => {
  editForm.value = { ...row }
  dialogVisible.value = true
}

const handleUpdate = async () => {
  try {
    await updateUser(editForm.value.id, editForm.value)
    ElMessage.success('更新成功')
    dialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('Failed to update user:', error)
    ElMessage.error('更新失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadUsers()
    } catch (error) {
      console.error('Failed to delete user:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-container {
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
