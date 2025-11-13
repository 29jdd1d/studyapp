<template>
  <div class="questions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <el-button type="primary" @click="handleAdd">新增题目</el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="题目内容">
          <el-input v-model="searchForm.content" placeholder="请输入题目内容" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="简答题" value="SHORT_ANSWER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="questionList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip width="300" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'SINGLE_CHOICE'">单选题</el-tag>
            <el-tag v-else-if="row.type === 'MULTIPLE_CHOICE'" type="success">多选题</el-tag>
            <el-tag v-else-if="row.type === 'TRUE_FALSE'" type="warning">判断题</el-tag>
            <el-tag v-else type="info">简答题</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="100" />
        <el-table-column prop="chapter" label="章节" width="150" />
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.difficulty === 'EASY'" type="success">简单</el-tag>
            <el-tag v-else-if="row.difficulty === 'MEDIUM'" type="warning">中等</el-tag>
            <el-tag v-else type="danger">困难</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <!-- Note: Edit functionality removed as PUT /question/{id} endpoint does not exist in backend -->
            <!-- <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button> -->
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
        @size-change="loadQuestions"
        @current-change="loadQuestions"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="题目内容">
          <el-input v-model="editForm.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="简答题" value="SHORT_ANSWER" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-input v-model="editForm.subject" placeholder="请输入科目" />
        </el-form-item>
        <el-form-item label="章节">
          <el-input v-model="editForm.chapter" placeholder="请输入章节" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="editForm.difficulty" placeholder="请选择难度" style="width: 100%">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="选项" v-if="editForm.type !== 'SHORT_ANSWER'">
          <el-input v-model="editForm.options" type="textarea" :rows="4" 
                    placeholder="请输入选项，每行一个，格式: A:选项内容" />
        </el-form-item>
        <el-form-item label="答案">
          <el-input v-model="editForm.answer" placeholder="请输入答案" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="editForm.explanation" type="textarea" :rows="3" placeholder="请输入解析" />
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
import { getQuestions, createQuestion, deleteQuestion } from '../../api/admin'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

const searchForm = reactive({
  content: '',
  type: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const questionList = ref([])
const editForm = ref({})

const dialogTitle = computed(() => isEdit.value ? '编辑题目' : '新增题目')

const loadQuestions = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getQuestions(params)
    if (response.data) {
      // Spring Data Page returns 'content' for the list and 'totalElements' for total count
      questionList.value = response.data.content || response.data
      pagination.total = response.data.totalElements || questionList.value.length
    }
  } catch (error) {
    console.error('Failed to load questions:', error)
    // Mock data
    questionList.value = [
      {
        id: 1,
        content: '下列函数中，哪个是连续函数？',
        type: 'SINGLE_CHOICE',
        subject: '高等数学',
        chapter: '第一章',
        difficulty: 'EASY',
        options: 'A:f(x)=1/x\nB:f(x)=x^2\nC:f(x)=|x|\nD:以上都是',
        answer: 'B',
        explanation: 'x^2是连续函数',
        createdAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        content: '极限lim(x→0) sinx/x的值是多少？',
        type: 'SINGLE_CHOICE',
        subject: '高等数学',
        chapter: '第一章',
        difficulty: 'MEDIUM',
        options: 'A:0\nB:1\nC:∞\nD:不存在',
        answer: 'B',
        explanation: '这是一个重要极限',
        createdAt: '2024-01-02 11:00:00'
      }
    ]
    pagination.total = questionList.value.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadQuestions()
}

const handleReset = () => {
  searchForm.content = ''
  searchForm.type = ''
  pagination.pageNum = 1
  loadQuestions()
}

const handleAdd = () => {
  isEdit.value = false
  editForm.value = {
    content: '',
    type: 'SINGLE_CHOICE',
    subject: '',
    chapter: '',
    difficulty: 'MEDIUM',
    options: '',
    answer: '',
    explanation: ''
  }
  dialogVisible.value = true
}

// Note: handleEdit function removed as PUT /question/{id} endpoint does not exist in backend
// const handleEdit = (row) => {
//   isEdit.value = true
//   editForm.value = { ...row }
//   dialogVisible.value = true
// }

const handleSave = async () => {
  try {
    if (isEdit.value) {
      // Note: Update functionality removed as PUT /question/{id} endpoint does not exist in backend
      ElMessage.warning('编辑功能暂不支持，请删除后重新创建')
      return
    } else {
      await createQuestion(editForm.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadQuestions()
  } catch (error) {
    console.error('Failed to save question:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该题目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteQuestion(row.id)
      ElMessage.success('删除成功')
      loadQuestions()
    } catch (error) {
      console.error('Failed to delete question:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.questions-container {
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
